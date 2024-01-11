package planning.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableMission
import planning.domain.Mission

class MissionRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    val selectedMission: MutableStateFlow<Mission?> = MutableStateFlow(null)

    suspend fun getMissions(): List<Mission> {
        return supabase.postgrest.from("mission").select()
            .also { println("LOADING MISSIONS") }.decodeList<Mission>()
    }

    suspend fun createMission(mission: InsertableMission): Mission {
        return supabase.postgrest.from("mission")
            .insert(mission) { select() }.decodeSingle() // error handling
    }
}