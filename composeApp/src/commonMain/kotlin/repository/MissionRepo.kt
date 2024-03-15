package repository

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Converter.Builder
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.domain.InsertableMission
import repository.domain.Mission
import repository.domain.MissionId
import repository.domain.NetworkMission
import repository.domain.Tables
import repository.domain.UserId
import repository.domain.toLocal


sealed class MissionKey {
    sealed class Read : MissionKey() {
        data object ByOwner : Read()
        data class ByID(val id: MissionId) : Read()
    }
}

class MissionRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    val selectedMission: MutableStateFlow<Mission?> = MutableStateFlow(null)

    private val converter = Builder<NetworkMission, Mission, Mission>().fromNetworkToLocal {
        it.toLocal()
    }.fromOutputToLocal { it }
        .build()

    private val userMissionStore: Store<MissionKey, List<Mission>> = StoreBuilder.from(
        fetcher = Fetcher.of { key: MissionKey ->
            require(key is MissionKey.Read)
            when (key) {
                is MissionKey.Read.ByOwner -> loadMissions().map {
                    converter.fromNetworkToLocal(it)
                }

                is MissionKey.Read.ByID -> loadMission(key.id).map {
                    converter.fromNetworkToLocal(it)
                }
            }
        },
    ).build()


    private suspend fun loadMissions(): List<NetworkMission> {
        return try {
            supabase.from(Tables.MISSION.path).select().decodeList<NetworkMission>()
        } catch (e: HttpRequestException) {
            Napier.e("Loading missions for userid failed", e)
            emptyList()
        }
    }

    fun getMissions(): Flow<StoreReadResponse<List<Mission>>> =
        userMissionStore.stream(
            StoreReadRequest.cached(
                MissionKey.Read.ByOwner, true
            )
        )

    fun getMission(id: MissionId): Flow<StoreReadResponse<List<Mission>>> =
        userMissionStore.stream(
            StoreReadRequest.cached(
                MissionKey.Read.ByID(
                    id
                ), true
            )
        )

    private suspend fun loadMission(id: MissionId): List<NetworkMission> {
        return try {
            listOf(supabase.from(Tables.MISSION.path).select {
                filter { eq("id", id) }
            }.decodeSingle<NetworkMission>())
        } catch (e: HttpRequestException) {
            Napier.e("Loading missions for userid failed", e)
            emptyList()
        }
    }


    suspend fun upsertMission(mission: InsertableMission): Mission =
        converter.fromNetworkToLocal(supabase.from(Tables.MISSION.path).upsert(mission) { select() }
            .decodeSingle<NetworkMission>())
}
