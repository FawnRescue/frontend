package planning.presentation.flightplan_editor

import presentation.maps.LatLong

sealed interface FlightPlanEditorEvent {
    data class MarkerAdded(val location: LatLong) : FlightPlanEditorEvent
    data class MarkerRemoved(val location: LatLong) : FlightPlanEditorEvent
    data object ToggleBoundaryMarkers : FlightPlanEditorEvent
    data object ToggleBoundary : FlightPlanEditorEvent
    data object ToggleCheckpointMarkers : FlightPlanEditorEvent
    data object TogglePath : FlightPlanEditorEvent
    data object SaveBoundary : FlightPlanEditorEvent
    data object ToggleLayers : FlightPlanEditorEvent
}
