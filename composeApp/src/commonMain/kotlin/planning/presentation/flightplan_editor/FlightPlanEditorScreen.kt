package planning.presentation.flightplan_editor


import androidx.compose.runtime.Composable
import presentation.maps.LatLong

@Composable
fun FlightPlanEditorScreen(onEvent: (FlightPlanEditorEvent) -> Unit, state: FlightPlanEditorState) {
    val testLocation = LatLong(51.5534005, 9.9746353)

    GoogleMaps(
        testLocation,
        onMapClick = {
            onEvent(FlightPlanEditorEvent.MarkerAdded(it))
        },
        state.boundary
    )
}

