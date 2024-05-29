package repository

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.domain.Aircraft
import repository.domain.AircraftId
import repository.domain.InsertableAircraft
import repository.domain.NetworkAircraft
import repository.domain.Tables
import repository.domain.UserId
import repository.domain.toLocal

sealed class AircraftKey {
    sealed class Read : AircraftKey() {
        data class ByOwner(val owner: UserId) : Read() //TODO use implicit UserID when selecting
        data class ByID(val id: AircraftId) : Read()
    }
}

class AircraftRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    private val converter =
        Converter.Builder<NetworkAircraft, Aircraft, Aircraft>().fromNetworkToLocal {
            it.toLocal()
        }.fromOutputToLocal { it }
            .build()

    private val store: Store<AircraftKey, List<Aircraft>> = StoreBuilder.from(
        fetcher = Fetcher.of { key: AircraftKey ->
            when (key) {
                is AircraftKey.Read.ByID -> loadAircraft(key.id).map {
                    converter.fromNetworkToLocal(
                        it
                    )
                }

                is AircraftKey.Read.ByOwner -> loadAircrafts(key.owner).map {
                    converter.fromNetworkToLocal(
                        it
                    )
                }

            }
        }
    ).build()

    private suspend fun loadAircrafts(userId: UserId): List<NetworkAircraft> {
        return try {
            supabase.from(Tables.AIRCRAFT.path)
                .select {
                    filter {
                        eq("deleted", false)
                        eq("owner", userId)
                    }
                }
                .decodeList<NetworkAircraft>()
        } catch (e: HttpRequestException) {
            Napier.e("Loading aircrafts for userid failed", e)
            emptyList()
        }
    }

    private suspend fun loadAircraft(id: AircraftId): List<NetworkAircraft> {
        return try {
            supabase.from(Tables.AIRCRAFT.path)
                .select {
                    filter {
                        eq("deleted", false)
                        eq("token", id)
                    }
                }
                .decodeList<NetworkAircraft>()
        } catch (e: HttpRequestException) {
            Napier.e("Loading aircraft by id failed", e)
            emptyList()
        }
    }

    fun getAircrafts(userId: UserId): Flow<StoreReadResponse<List<Aircraft>>> {
        return store.stream(StoreReadRequest.cached(AircraftKey.Read.ByOwner(userId), true))
    }

    fun getAircraft(id: AircraftId): Flow<StoreReadResponse<List<Aircraft>>> {
        return store.stream(StoreReadRequest.cached(AircraftKey.Read.ByID(id), true))
    }


    suspend fun deleteAircraft(aircraftId: AircraftId) {
        supabase.from(Tables.AIRCRAFT.path).update({ set("deleted", true) }) {
            filter {
                eq("token", aircraftId)
            }
        }
    }

    suspend fun upsertAircraft(aircraft: InsertableAircraft): Aircraft =
        converter.fromNetworkToLocal(supabase.from(Tables.AIRCRAFT.path).upsert(aircraft) { select() }
            .decodeSingle<NetworkAircraft>())
}