package planning.presentation.flightplan_editor


import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import core.utils.RescueIcons
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.MarkerAdded
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.MarkerRemoved
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.SaveBoundary
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleBoundary
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleBoundaryMarkers
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleCheckpointMarkers
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.TogglePath
import presentation.maps.LatLong
import presentation.maps.getCenter

@Composable
fun FlightPlanEditorScreen(onEvent: (FlightPlanEditorEvent) -> Unit, state: FlightPlanEditorState) {
    val testLocation = LatLong(51.5534005, 9.9746353)

    GoogleMaps(
        data = GoogleMapsData(
            initialPosition = if (state.selectedFlightPlan != null) state.selectedFlightPlan.boundary.getCenter() else testLocation,
            boundary = state.editedBoundary,
            checkpoints = state.editedCheckpoints ?: listOf(),
        ),
        config = GoogleMapsConfig(
            showBoundaryMarkers = state.showBoundaryMarkers,
            showBoundary = state.showBoundary,
            showCheckpointMarkers = state.showCheckpointMarkers,
            showPath = state.showPath
        ),
        functions = GoogleMapsFunctions(
            onMapClick = {
                onEvent(MarkerAdded(it))
            },
            onMarkerClick = {
                onEvent(MarkerRemoved(it))
            },
        ),

        )
    FloatingActionButton(
        onClick = { onEvent(SaveBoundary) },
    ) {
        Icon(RescueIcons.Save, "Save")
    }
    FloatingActionButton(
        onClick = { onEvent(ToggleBoundaryMarkers) },
        modifier = Modifier.offset(100.dp).zIndex(1f)
            .alpha(if (state.showBoundaryMarkers) 1.0f else 0.5f)
    ) {
        Text("BMarkers")
    }
    FloatingActionButton(
        onClick = { onEvent(ToggleCheckpointMarkers) },
        modifier = Modifier.offset(160.dp).zIndex(1f)
            .alpha(if (state.showCheckpointMarkers) 1.0f else 0.5f)
    ) {
        Text("CMarkers")
    }
    FloatingActionButton(
        onClick = { onEvent(ToggleBoundary) },
        modifier = Modifier.offset(220.dp).zIndex(1f).alpha(if (state.showBoundary) 1.0f else 0.5f)
    ) {
        Text("Boundary")
    }
    FloatingActionButton(
        onClick = { onEvent(TogglePath) },
        modifier = Modifier.offset(280.dp).zIndex(1f).alpha(if (state.showPath) 1.0f else 0.5f)
    ) {
        Text("Path")
    }
}

