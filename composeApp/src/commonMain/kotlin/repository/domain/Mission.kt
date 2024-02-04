package repository.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import repository.FlightPlanId
import repository.MissionId
import repository.UserId


@Serializable
data class Mission(
    val id: MissionId,
    val created_at: LocalDateTime,
    val description: String,
    val owner: UserId,
    val plan: FlightPlanId?,
)

@Serializable
data class NetworkMission(
    val id: String,
    val created_at: String,
    val description: String,
    val owner: String,
    val plan: String?,
)

fun NetworkMission.toLocal(): Mission {
    return Mission(
        MissionId(this.id),
        LocalDateTime.parse(this.created_at.split("+")[0]), //TODO Fix parsing
        this.description,
        UserId(this.owner),
        this.plan?.let { FlightPlanId(it) }
    )
}

fun Mission.insertable(): InsertableMission {
    return InsertableMission(this.description, this.id, this.plan)
}

@Serializable
data class InsertableMission(
    val description: String,
    val id: MissionId? = null,
    val plan: FlightPlanId? = null,
)
