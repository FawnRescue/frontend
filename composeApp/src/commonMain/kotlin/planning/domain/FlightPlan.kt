package planning.domain

import kotlinx.serialization.Serializable
import presentation.maps.LatLong
import presentation.maps.getCenter

@Serializable
data class FlightPlan(
    val id: String,
    val checkpoints: List<LatLong>?,
    val boundary: List<LatLong>,
    val location: LatLong,
    val created_at: String,
)

fun FlightPlan.insertable(): InsertableFlightPlan {
    return InsertableFlightPlan(this.boundary, this.location, this.id)
}

@Serializable
data class InsertableFlightPlan(
    val boundary: List<LatLong>,
    val location: LatLong,
    val id: String? = null,
)


