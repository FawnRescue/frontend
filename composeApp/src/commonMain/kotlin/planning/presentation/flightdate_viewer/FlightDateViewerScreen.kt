package planning.presentation.flightdate_viewer


import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import core.utils.RescueIcons
import pilot.DetectionDialog
import planning.presentation.flightplan_editor.GoogleMaps
import planning.presentation.flightplan_editor.GoogleMapsConfig
import planning.presentation.flightplan_editor.GoogleMapsData
import planning.presentation.flightplan_editor.GoogleMapsFunctions
import presentation.maps.LatLong
import presentation.maps.getCenter

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
        data = GoogleMapsData(
            initialPosition = if (state.selectedFlightPlan != null) state.selectedFlightPlan.boundary.getCenter() else testLocation,
            boundary = state.boundary,
            checkpoints = state.checkpoints ?: listOf(),
            detections = if (state.showDetectionMarkers) state.detections else emptyList(),

            ),
        config = GoogleMapsConfig(
            showBoundaryMarkers = state.showBoundaryMarkers,
            showBoundary = state.showBoundary,
            showCheckpointMarkers = state.showCheckpointMarkers,
            showPath = state.showPath,
        ),
        functions = GoogleMapsFunctions(
            onDetectionMarkerClick = { onEvent(FlightDateViewerEvent.DetectionSelected(it)) }
        ),
    )
    FloatingActionButton(
        onClick = { onEvent(FlightDateViewerEvent.Cancel) },
    ) {
        Icon(RescueIcons.Cancel, "Save")
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

