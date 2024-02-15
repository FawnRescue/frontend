package planning.presentation.mission_editor

import repository.domain.FlightDate
import repository.domain.InsertableMission


sealed interface MissionEditorEvent {
    data class UpdateMission(val mission: InsertableMission) : MissionEditorEvent
    data object SaveMission : MissionEditorEvent
    data object ResetMission : MissionEditorEvent
    data object Cancel : MissionEditorEvent
    data object EditFlightPlan : MissionEditorEvent
    data object AddFlightDate : MissionEditorEvent
    data class DateSelected(val date: FlightDate) : MissionEditorEvent
}
