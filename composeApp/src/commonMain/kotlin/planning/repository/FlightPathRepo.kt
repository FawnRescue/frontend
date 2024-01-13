package planning.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableFlightPlan
import planning.domain.FlightPlan
import planning.domain.InsertableMission
import planning.domain.Tables

class FlightPlanRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    suspend fun getPath(id: String): FlightPlan = supabase.from(Tables.FLIGHT_PLAN.path).select(
    ) {
        filter {
            eq("id", id)
        }
    }.decodeSingle()

    suspend fun upsertFlightPlan(
        selectedMission: InsertableMission,
        plan: InsertableFlightPlan
    ): FlightPlan {
        println(plan)
        if (selectedMission.id == null) {
            println("error: mission has no id, cant upsert flight plan")
            println(selectedMission)
            throw Error("error: mission has no id, cant upsert flight plan")
        }
        return supabase.from(Tables.FLIGHT_PLAN.path)
            .upsert(plan) { select() }.decodeSingle<FlightPlan>().also {
                supabase.from(Tables.MISSION.path).update(selectedMission.copy(plan = it.id)) {
                    filter {
                        eq("id", selectedMission.id)
                    }
                }
            } // error handling
    }
}