package pilot

import androidx.compose.runtime.Composable
import planning.presentation.flightplan_editor.GoogleMaps
import presentation.maps.getCenter

@Composable
fun PilotScreen(onEvent: (PilotEvent) -> Unit, state: PilotState) {
    if(state.plan == null || state.date == null || state.mission == null){
        return
    }
    GoogleMaps(
        state.plan.boundary.getCenter(),
        onMapClick = {
        },
        onMarkerClick = {
        },
        listOf(),
        state.plan.checkpoints ?: listOf(),
        showBoundaryMarkers = false,
        showBoundary = false,
        showCheckpointMarkers = false,
        showPath = true,
    )
}

