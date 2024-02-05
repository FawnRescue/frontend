package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.domain.InsertableMission
import repository.domain.Mission
import repository.domain.Tables

class MissionRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    val selectedMission: MutableStateFlow<Mission?> = MutableStateFlow(null)

    suspend fun getMissions(): List<Mission> =
        supabase.from(Tables.MISSION.path).select().decodeList<Mission>()

    suspend fun getMission(id: String): Mission =
        supabase.from(Tables.MISSION.path).select {
            filter { eq("id", id) }
        }.decodeSingle<Mission>()

    suspend fun upsertMission(mission: InsertableMission): Mission =
        supabase.from(Tables.MISSION.path)
            .upsert(mission) { select() }.decodeSingle() // error handling
}