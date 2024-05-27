package repository.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


data class Aircraft(
    val token: AircraftId,
    val owner: UserId,
    val name: String,
    val description: String?,
    val created_at: LocalDateTime,
    val deleted: Boolean,
    val cameraFOV: Double,
    val flightHeight: Double,
)

@Serializable
data class InsertableAircraft(
    val token: String,
    val owner: String? = null,
    val name: String,
    val description: String? = null,
    val created_at: String? = null,
    val deleted: Boolean? = null,
    val camera_fov: Double? = null,
    val flight_height: Double? = null,
)

@Serializable
data class NetworkAircraft(
    val token: String,
    val owner: String,
    val name: String,
    val description: String?,
    val created_at: String,
    val deleted: Boolean,
    val camera_fov: Double,
    val flight_height: Double,
)

fun NetworkAircraft.toLocal(): Aircraft {
    return Aircraft(
        token = AircraftId(this.token),
        created_at = LocalDateTime.parse(this.created_at.split("+")[0]), //TODO Fix parsing
        description = this.description,
        owner = UserId(this.owner),
        deleted = this.deleted,
        name = this.name,
        cameraFOV = this.camera_fov,
        flightHeight = this.flight_height
    )
}