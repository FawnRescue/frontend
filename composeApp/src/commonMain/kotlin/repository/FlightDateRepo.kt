package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.domain.FlightDate
import repository.domain.InsertableFlightDate
import repository.domain.MissionId
import repository.domain.Tables

class FlightDateRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    val selectedFlightDate: MutableStateFlow<FlightDate?> = MutableStateFlow(null)

    private val store = StoreBuilder.from(fetcher = Fetcher.of { key: MissionId ->
        loadDates(key)
    }).build()

    private suspend fun loadDates(missionId: MissionId): List<FlightDate> =
        try {
            supabase.from(Tables.FLIGHT_DATE.path).select {
                filter {
                    eq("mission", missionId)
                }
            }.decodeList<FlightDate>()
        } catch (e: HttpRequestException) {
            listOf()
        }

    fun getDates(missionId: MissionId): Flow<StoreReadResponse<List<FlightDate>>> {
        return store.stream(StoreReadRequest.cached(missionId, true))
    }

    suspend fun upsertFlightDate(date: InsertableFlightDate): FlightDate =
        supabase.from(Tables.FLIGHT_DATE.path)
            .upsert(date) { select() }.decodeSingle() // error handling
}