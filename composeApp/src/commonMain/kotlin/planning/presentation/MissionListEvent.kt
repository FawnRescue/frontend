package planning.presentation

import planning.presentation.domain.Mission

sealed interface MissionListEvent {
    object CreateNewMission : MissionListEvent
    data class ExistingMissionSelected(val mission: Mission) : MissionListEvent
}
