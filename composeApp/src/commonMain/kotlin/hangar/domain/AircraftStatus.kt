package hangar.domain

import kotlinx.serialization.Serializable

@Serializable
data class Battery(val remainingPercent: Float?, val voltage: Float)

@Serializable
data class Location(val longitude: Double, val latitude: Double)

@Serializable
data class AircraftStatus(
    val state: AircraftState,
    val battery: Battery?,
    val location: Location?,
    val altitude: Float?,
    val numSatellites: Int?,
)