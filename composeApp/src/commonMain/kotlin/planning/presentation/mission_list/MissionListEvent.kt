package planning.presentation.mission_list

import planning.domain.Mission

sealed interface MissionListEvent {
    object CreateNewMission : MissionListEvent
    data class ExistingMissionSelected(val mission: Mission) : MissionListEvent
}
