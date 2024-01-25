package presentation.maps

import kotlinx.serialization.Serializable

@Serializable
data class LatLong(val latitude: Double = 0.0, val longitude: Double = 0.0)

fun List<LatLong>.getCenter(): LatLong {
    // Treating earth as flat, as we deal with relatively close points.
    // Find the center of the polygon by averaging the lat and lons
    return LatLong(
        this.sumOf { it.latitude } / this.size,
        this.sumOf { it.longitude } / this.size
    )
}
