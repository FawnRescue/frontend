package planning.presentation.flightplan_editor

import presentation.maps.LatLong

sealed interface FlightPlanEditorEvent {
    data class MarkerAdded(val location: LatLong) : FlightPlanEditorEvent
}
