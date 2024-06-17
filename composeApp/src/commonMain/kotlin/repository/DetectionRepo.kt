package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.domain.Detection
import repository.domain.FlightDateId
import repository.domain.NetworkDetection
import repository.domain.Tables
import repository.domain.toLocal

class DetectionRepo : KoinComponent {
    private val supabase: SupabaseClient by inject<SupabaseClient>()

    private val store = StoreBuilder.from(fetcher = Fetcher.of { key: FlightDateId ->
        loadDetectionsByFlightDate(key)
    }).build()

    private suspend fun loadDetectionsByFlightDate(flightDateId: FlightDateId): List<Detection> {
        return supabase.from(Tables.DETECTION.path)
            .select {
                filter {
                    eq("flight_date", flightDateId)
                }
            }.decodeList<NetworkDetection>().map { it.toLocal() }
    }

    fun getDetections(flightDateId: FlightDateId): Flow<StoreReadResponse<List<Detection>>> {
        return store.stream(StoreReadRequest.cached(flightDateId, true))
    }

    suspend fun deleteDetections(flightDateId: FlightDateId){
        supabase.from(Tables.DETECTION.path).delete {
            filter {
                eq("flight_date", flightDateId.id)
            }
        }
    }
}