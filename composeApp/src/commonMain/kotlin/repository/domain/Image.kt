package repository.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import presentation.maps.LatLong

@Serializable
data class NetworkImage(
    val id: String,
    val created_at: String,
    val rgb_path: String?,
    val thermal_path: String?,
    val binary_path: String?,
    val location: LatLong,
    val flight_date: String,
    val processed: Boolean,
    val owner: String,
)

fun NetworkImage.toLocal(): Image {
    return Image(
        ImageId(id),
        LocalDateTime.parse(created_at.split("+")[0]), //TODO Fix parsing
        rgb_path,
        thermal_path,
        binary_path,
        location,
        FlightDateId(flight_date),
        processed,
        UserId(owner)
    )
}

data class Image(
    val id: ImageId,
    val created_at: LocalDateTime,
    val rgb_path: String?,
    val thermal_path: String?,
    val binary_path: String?,
    val location: LatLong,
    val flight_date: FlightDateId,
    val processed: Boolean,
    val owner: UserId,
)