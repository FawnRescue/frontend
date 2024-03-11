package repository

import kotlinx.coroutines.flow.Flow
import presentation.maps.LatLong

actual class LocationService {
    actual suspend fun getCurrentLocation(): LatLong? {
        TODO("Not yet implemented")
    }

    actual fun location(): Flow<LatLong> {
        TODO("Not yet implemented")
    }
}