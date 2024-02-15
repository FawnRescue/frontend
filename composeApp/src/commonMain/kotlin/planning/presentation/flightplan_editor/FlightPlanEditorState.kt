package planning.presentation.flightplan_editor

import presentation.maps.LatLong
import repository.domain.FlightPlan
import repository.domain.Mission

data class FlightPlanEditorState(
    val editedBoundary: List<LatLong> = listOf(),
    val editedCheckpoints: List<LatLong>? = listOf(),
    val selectedMission: Mission? = null,
    val selectedFlightPlan: FlightPlan? = null,
    val showBoundaryMarkers: Boolean = true,
    val showBoundary: Boolean = true,
    val showCheckpointMarkers: Boolean = false,
    val showPath: Boolean = true,
)