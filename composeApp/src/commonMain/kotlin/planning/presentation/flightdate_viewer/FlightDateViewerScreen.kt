package planning.presentation.flightdate_viewer


import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import pilot.DetectionDialog
import pilot.PilotEvent
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.MarkerAdded
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.MarkerRemoved
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.SaveBoundary
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleBoundary
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleBoundaryMarkers
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleCheckpointMarkers
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.TogglePath
import planning.presentation.flightplan_editor.GoogleMaps
import presentation.maps.LatLong
import presentation.maps.getCenter
import repository.domain.Detection

@Composable
fun FlightDateViewerScreen(onEvent: (FlightDateViewerEvent) -> Unit, state: FlightDateViewerState) {
    val testLocation = LatLong(51.5534005, 9.9746353)

    if (state.selectedDetection != null) {
        DetectionDialog(
            state.selectedDetection,
            state.selectedDetectionRGBImageData,
            state.selectedDetectionThermalImageData,
            onDismiss = { onEvent(FlightDateViewerEvent.DetectionDeselected) })
    }

    GoogleMaps(
        if (state.selectedFlightPlan != null) state.selectedFlightPlan.boundary.getCenter() else testLocation,
        onMapClick = {},
        onMarkerClick = {},
        state.boundary,
        state.checkpoints ?: listOf(),
        showBoundaryMarkers = state.showBoundaryMarkers,
        showBoundary = state.showBoundary,
        showCheckpointMarkers = state.showCheckpointMarkers,
        showPath = state.showPath,
        detections = if (state.showDetectionMarkers) state.detections else emptyList(),
        onDetectionMarkerClick = { onEvent(FlightDateViewerEvent.DetectionSelected(it)) }
    )
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.Cancel) },
    ) {
        Icon(Icons.Rounded.Cancel, "Save")
    }
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.ToggleBoundaryMarkers) },
        modifier = Modifier
            .offset(80.dp)
            .zIndex(1f)
            .alpha(if (state.showBoundaryMarkers) 1.0f else 0.5f)
    ) {
        Text("BMarkers")
    }
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.ToggleCheckpointMarkers) },
        modifier = Modifier
            .offset(160.dp)
            .zIndex(1f)
            .alpha(if (state.showCheckpointMarkers) 1.0f else 0.5f)
    ) {
        Text("CMarkers")
    }
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.ToggleBoundary) },
        modifier = Modifier
            .offset(240.dp)
            .zIndex(1f)
            .alpha(if (state.showBoundary) 1.0f else 0.5f)
    ) {
        Text("Boundary")
    }
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.TogglePath) },
        modifier = Modifier
            .offset(320.dp)
            .zIndex(1f)
            .alpha(if (state.showPath) 1.0f else 0.5f)
    ) {
        Text("Path")
    }
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.ToggleDetections) },
        modifier = Modifier
            .offset(400.dp)
            .zIndex(1f)
            .alpha(if (state.showDetectionMarkers) 1.0f else 0.5f)
    ) {
        Text("Detection")
    }
}

