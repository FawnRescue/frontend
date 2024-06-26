package repository.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCommand(
    val id: String,
    val created_at: String,
    val owner: String,
    val command: Commands,
    val status: CommandStatus,
    val aircraft: String,
    // val flightDateId: String,
    val context: String,
)

data class Command(
    val command: Commands,
    //val flightDateId: FlightDateId,
    val context: String,
    val status: CommandStatus,
    val id: CommandId,
    val created_at: LocalDateTime,
    val owner: UserId,
    val aircraft: AircraftId,
)

@Serializable
data class InsertableCommand(
    val command: Commands,
    //val flightDateId: FlightDateId,
    val context: String,
    val aircraft: AircraftId,
)

fun NetworkCommand.toLocal(): Command {
    return Command(
        id = CommandId(id),
        created_at = LocalDateTime.parse(created_at.split("+")[0]), //TODO Fix parsing
        owner = UserId(owner),
        command = command,
        aircraft = AircraftId(aircraft),
        context = context,
        //flightDateId = FlightDateId(flightDateId),
        status = status
    )
}

enum class CommandStatus {
    PENDING,
    EXECUTED,
    FAILED,
    SKIPPED
}

enum class Commands {
    ARM,
    DISARM,
    TAKEOFF,
    LAND,
    RETURN,

    //Unimplemented:
    FLY2CHECKPOINT,
    CAPTURE_IMAGE,
    LOITER,
    RTH,
    KILL,
    ELAND,
    CONTINUE
}