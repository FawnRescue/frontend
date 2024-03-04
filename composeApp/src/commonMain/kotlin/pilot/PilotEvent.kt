package pilot

import repository.domain.InsertableCommand

sealed interface PilotEvent {
    data object NoPlan : PilotEvent
    data class SendCommand(val command: InsertableCommand) : PilotEvent
}
