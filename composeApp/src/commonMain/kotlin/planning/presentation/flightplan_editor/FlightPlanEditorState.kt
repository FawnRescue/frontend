package planning.presentation.flightplan_editor

import planning.domain.Mission
import presentation.maps.LatLong

data class FlightPlanEditorState(
    val selectedMission: Mission?,
    val boundary: List<LatLong> = listOf()
)