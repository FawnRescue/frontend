package planning.presentation.mission_list

import repository.domain.FlightDate
import repository.domain.Mission

sealed interface MissionListEvent {
    data object CreateNewMission : MissionListEvent
    data class ExistingMissionSelected(val mission: Mission) : MissionListEvent
}
