package planning.presentation.flightplan_editor


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import presentation.maps.LatLong
import presentation.maps.getCenter

@Composable
fun FlightPlanEditorScreen(onEvent: (FlightPlanEditorEvent) -> Unit, state: FlightPlanEditorState) {
    val testLocation = LatLong(51.5534005, 9.9746353)
    GoogleMaps(
        if (state.selectedFlightPlan != null) state.selectedFlightPlan.boundary.getCenter() else testLocation,
        onMapClick = {
            onEvent(FlightPlanEditorEvent.MarkerAdded(it))
        },
        onMarkerClick = {
          onEvent(FlightPlanEditorEvent.MarkerRemoved(it))
        },
        state.editedBoundary,
        state.editedCheckpoints ?: listOf()
    )
    FloatingActionButton(
        onClick = { onEvent(FlightPlanEditorEvent.SaveBoundary) },
    ) {
        Icon(Icons.Rounded.Save, "Save")
    }
}

