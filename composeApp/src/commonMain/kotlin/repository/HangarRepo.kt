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
import repository.domain.NetworkAircraft
import repository.domain.Tables
import repository.domain.UserId
import repository.domain.toLocal

class HangarRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    private val converter =
        Converter.Builder<NetworkAircraft, Aircraft, Aircraft>().fromNetworkToLocal {
            it.toLocal()
        }.fromOutputToLocal { it }
            .build()

    private val store: Store<UserId, List<Aircraft>> = StoreBuilder.from(
        fetcher = Fetcher.of { key: UserId ->
            loadAircrafts(key).map { converter.fromNetworkToLocal(it) }
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
            Napier.e("Loading missions for userid failed", e)
            emptyList()
        }
    }

    fun getAircrafts(userId: UserId): Flow<StoreReadResponse<List<Aircraft>>> {
        return store.stream(StoreReadRequest.cached(userId, true))
    }


    suspend fun deleteAircraft(aircraftId: AircraftId) {
        supabase.from(Tables.AIRCRAFT.path).update({ set("deleted", true) }) {
            filter {
                eq("token", aircraftId)
            }
        }
    }
}