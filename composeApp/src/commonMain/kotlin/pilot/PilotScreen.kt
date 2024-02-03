package pilot

import pilot.PilotEvent.*
import androidx.compose.runtime.Composable
import planning.presentation.flightplan_editor.GoogleMaps
import presentation.maps.getCenter

@Composable
fun PilotScreen(onEvent: (PilotEvent) -> Unit, state: PilotState) {
    if(state.plan == null){
        onEvent(NoPlan)
        return
    }
    GoogleMaps(
        state.plan.boundary.getCenter(),
        onMapClick = {
        },
        onMarkerClick = {
        },
        listOf(),
        state.plan.checkpoints ?: listOf()
    )
}

