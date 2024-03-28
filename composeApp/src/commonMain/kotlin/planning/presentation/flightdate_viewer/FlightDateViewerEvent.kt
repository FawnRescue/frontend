package planning.presentation.flightdate_viewer

import pilot.PilotEvent
import repository.domain.Detection


sealed interface FlightDateViewerEvent {
    data object ToggleBoundaryMarkers : FlightDateViewerEvent
    data object ToggleBoundary : FlightDateViewerEvent
    data object ToggleCheckpointMarkers : FlightDateViewerEvent
    data object TogglePath : FlightDateViewerEvent
    data object ToggleDetections : FlightDateViewerEvent
    data object Cancel : FlightDateViewerEvent
    data class DetectionSelected(val detection: Detection) : FlightDateViewerEvent
    data object DetectionDeselected : FlightDateViewerEvent

}
