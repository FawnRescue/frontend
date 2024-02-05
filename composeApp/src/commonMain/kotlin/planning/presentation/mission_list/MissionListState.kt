package planning.presentation.mission_list

import repository.domain.FlightDate
import repository.domain.Mission

data class MissionListState(
    val missions: List<Mission>,
)