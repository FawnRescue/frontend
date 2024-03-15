package repository.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class NetworkFlightDate(
    val id: String,
    val created_at: Instant,
    val mission: String,
    val start_date: Instant,
    val end_date: Instant,
    val aircraft: String,
)

fun NetworkFlightDate.insertable(): InsertableFlightDate {
    return InsertableFlightDate(
        this.id,
        MissionId(this.mission),
        this.start_date,
        this.end_date,
        this.aircraft
    )
}

fun NetworkFlightDate.editable(): EditableFlightDate {
    return EditableFlightDate(
        this.id,
        this.mission,
        this.start_date,
        this.end_date,
        this.aircraft
    )
}

@Serializable
data class EditableFlightDate(
    val id: String?,
    val mission: String,
    val start_date: Instant?,
    val end_date: Instant?,
    val aircraft: String?,
)

@Serializable
data class InsertableFlightDate(
    val id: String? = null,
    val mission: MissionId,
    val start_date: Instant,
    val end_date: Instant,
    val aircraft: String,
)