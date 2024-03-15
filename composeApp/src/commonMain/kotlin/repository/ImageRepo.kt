package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.domain.FlightDateId
import repository.domain.FlightPlan
import repository.domain.FlightPlanId
import repository.domain.Image
import repository.domain.ImageId
import repository.domain.NetworkImage
import repository.domain.toLocal

sealed class ImageKey {
    sealed class Read : ImageKey() {
        data class ByFlightDate(val flightDateId: FlightDateId) : Read()
        data class ById(val id: ImageId) : Read()
    }
}

class ImageRepo : KoinComponent {
    private val supabase: SupabaseClient by inject<SupabaseClient>()

    private val store = StoreBuilder.from(fetcher = Fetcher.of { key: ImageKey ->
        when (key) {
            is ImageKey.Read.ByFlightDate -> loadImagesByFlightDate(key.flightDateId)
            is ImageKey.Read.ById -> loadImageById(key.id)
        }
    }).build()

    private suspend fun loadImagesByFlightDate(flightDateId: FlightDateId): List<Image> {
        return supabase.from("image")
            .select {
                filter {
                    eq("flight_date", flightDateId)
                }
            }.decodeList<NetworkImage>().map { it.toLocal() }
    }

    private suspend fun loadImageById(id: ImageId): List<Image> {
        return supabase.from("image")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeList<NetworkImage>().map { it.toLocal() }
    }

    fun getImage(imageId: ImageId): Flow<StoreReadResponse<List<Image>>> {
        return store.stream(StoreReadRequest.cached(ImageKey.Read.ById(imageId), true))
    }

    fun getImageCached(imageId: ImageId): Flow<StoreReadResponse<List<Image>>> {
        return store.stream(StoreReadRequest.cached(ImageKey.Read.ById(imageId), false))
    }
}
