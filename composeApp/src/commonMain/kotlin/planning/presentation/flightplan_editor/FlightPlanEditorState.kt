package planning.presentation.flightplan_editor

import planning.domain.FlightPlan
import planning.domain.Mission
import presentation.maps.LatLong

data class FlightPlanEditorState(
    val editedBoundary: List<LatLong> = listOf(),
    val selectedMission: Mission? = null,
    val selectedFlightPlan: FlightPlan? = null
)