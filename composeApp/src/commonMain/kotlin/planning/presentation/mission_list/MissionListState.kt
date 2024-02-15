package planning.presentation.mission_list

import repository.domain.Mission

data class MissionListState(
    val missions: List<Mission>,
    val loading: Boolean = true,
)