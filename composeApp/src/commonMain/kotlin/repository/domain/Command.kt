package repository.domain

import kotlinx.serialization.Serializable

data class Command(
    val command: Commands,
    val flightPlanId: FlightPlanId,
    val context: Serializable,
    val state: CommandState,
)

enum class CommandState {
    PENDING,
    EXECUTED,
    FAILED,
    SKIPPED
}

enum class Commands {
    ARM,
    FLY2CHECKPOINT,
    CAPTURE_IMAGE,
    LOITER,
    RTH,
    KILL,
    ELAND
}