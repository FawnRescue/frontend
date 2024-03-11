package repository

import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.maps.LatLong

expect class LocationService: KoinComponent {
    suspend fun getCurrentLocation(): LatLong?
    fun location(): Flow<LatLong>
}
