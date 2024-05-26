package planning.presentation.mission_list

import repository.domain.Mission

data class MissionListState(
    val ownMissions: List<Mission>,
    val otherMissions: List<Mission>,
    val loading: Boolean = true,
)