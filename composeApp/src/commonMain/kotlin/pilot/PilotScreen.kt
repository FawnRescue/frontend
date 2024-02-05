package pilot

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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

