package pilot

import repository.domain.Detection
import repository.domain.InsertableCommand

sealed interface PilotEvent {
    data object NoPlan : PilotEvent
    data class SendCommand(val command: InsertableCommand) : PilotEvent
    data class DetectionSelected(val detection: Detection): PilotEvent
    data object DetectionDeselected: PilotEvent
}
