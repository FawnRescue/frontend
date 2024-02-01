package planning.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.FlightDate
import planning.domain.InsertableFlightDate
import planning.domain.InsertableMission
import planning.domain.Mission
import planning.domain.Tables

class FlightDateRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    val selectedFlightDate: MutableStateFlow<FlightDate?> = MutableStateFlow(null)
    suspend fun getDates(mission: String): List<FlightDate> =
        supabase.from(Tables.FLIGHT_DATE.path).select{
            filter {
                eq("mission", mission)
            }
        }.decodeList<FlightDate>()


    suspend fun upsertFlightDate(date: InsertableFlightDate): FlightDate =
        println(date).run { supabase.from(Tables.FLIGHT_DATE.path)
            .upsert(date) { select() }.decodeSingle()} // error handling
}