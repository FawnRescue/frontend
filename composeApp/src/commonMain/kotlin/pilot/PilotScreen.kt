package pilot

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import planning.presentation.flightplan_editor.GoogleMaps
import presentation.maps.getCenter

@Composable
fun PilotScreen(onEvent: (PilotEvent) -> Unit, state: PilotState) {
    if (state.loading) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        return
    }
    if (state.plan == null ||
        state.date == null ||
        state.mission == null ||
        state.aircraft == null
    ) {
        Text("Error loading necessary information") // TODO: Snackbar
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

