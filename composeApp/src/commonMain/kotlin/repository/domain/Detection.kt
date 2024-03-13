package repository.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import presentation.maps.LatLong

@Serializable
data class NetworkDetection(
    val id: String,
    val created_at: String,
    val image: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val confidence: Float?,
    val flight_date: String,
    val location: LatLong
)

fun NetworkDetection.toLocal(): Detection {
    return Detection(
        DetectionId(id),
        LocalDateTime.parse(created_at.split("+")[0]), //TODO Fix parsing
        ImageId(image),
        x, y, width, height, confidence,
        FlightDateId(flight_date),
        location
    )
}

data class Detection(
    val id: DetectionId,
    val created_at: LocalDateTime,
    val image: ImageId,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val confidence: Float?,
    val flight_date: FlightDateId,
    val location: LatLong
)