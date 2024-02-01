package planning.presentation.mission_list

import planning.domain.FlightDate
import planning.domain.Mission

data class MissionListState(
    val missions: List<Mission>,
)