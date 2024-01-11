package planning.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableMission
import planning.domain.Mission

class MissionRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    //TODO(): check whether everyone can use the same channel and get only their mission updates
    private val missionChannel = supabase.realtime.createChannel("mission_changes")

    val selectedMission: MutableStateFlow<Mission?> = MutableStateFlow(null)

    suspend fun getMissionTableChangeFlow(): Flow<PostgresAction> {
        if(supabase.realtime.status.value == Realtime.Status.DISCONNECTED){
            supabase.realtime.connect()
        }
        if(this.missionChannel.status.value == RealtimeChannel.Status.CLOSED){
            this.missionChannel.join()
        }

        val changeFlow = this.missionChannel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "mission"
        }

        return changeFlow
    }

    suspend fun getMissions(): List<Mission> {
        return supabase.postgrest.from("mission").select()
            .also { println("LOADING MISSIONS") }.decodeList<Mission>()
    }

    suspend fun createMission(mission: InsertableMission): Mission {
        return supabase.postgrest.from("mission")
            .insert(mission).decodeAs<List<Mission>>()[0] // error handling
    }
}