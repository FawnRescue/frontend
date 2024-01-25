package planning.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableMission
import planning.domain.Mission
import planning.domain.Tables

class MissionRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    val selectedMission: MutableStateFlow<Mission?> = MutableStateFlow(null)

    suspend fun getMissions(): List<Mission> =
        supabase.from(Tables.MISSION.path).select().decodeList<Mission>()


    suspend fun upsertMission(mission: InsertableMission): Mission =
        supabase.from(Tables.MISSION.path)
            .upsert(mission) { select() }.decodeSingle() // error handling
}