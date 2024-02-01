package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.domain.FlightDate
import repository.domain.InsertableFlightDate
import repository.domain.InsertableMission
import repository.domain.Mission
import repository.domain.Tables

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
        supabase.from(Tables.FLIGHT_DATE.path)
            .upsert(date) { select() }.decodeSingle() // error handling
}