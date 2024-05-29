package planning.presentation.flightplan_editor


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.ToggleLayers
import planning.presentation.flightplan_editor.FlightPlanEditorEvent.TogglePath
import presentation.maps.LatLong
import presentation.maps.getCenter


@Composable
fun FlightPlanEditorScreen(onEvent: (FlightPlanEditorEvent) -> Unit, state: FlightPlanEditorState) {
    val testLocation = LatLong(51.5534005, 9.9746353)
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(SaveBoundary) },
            ) {
                Icon(RescueIcons.Save, "Save")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
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
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { onEvent(ToggleLayers) },
                modifier = Modifier.zIndex(1f),
                shape = if (state.showLayers) CircleShape else FloatingActionButtonDefaults.shape
            ) {
                Icon(RescueIcons.Layers, "Layers")
            }

            AnimatedVisibility(state.showLayers) {
                Column {
                    Spacer(Modifier.height(2.dp))
                    FloatingActionButton(
                        onClick = { onEvent(ToggleBoundaryMarkers) },
                        modifier = Modifier.zIndex(1f)
                            .alpha(if (state.showBoundaryMarkers) 1.0f else 0.5f)
                    ) {
                        Icon(RescueIcons.LocationOn, "Boundary Marker")
                    }
                    Spacer(Modifier.height(2.dp))
                    FloatingActionButton(
                        onClick = { onEvent(ToggleCheckpointMarkers) },
                        modifier = Modifier.zIndex(1f)
                            .alpha(if (state.showCheckpointMarkers) 1.0f else 0.5f)
                    ) {
                        Icon(RescueIcons.PhotoCamera, "Checkpoint Marker")

                    }
                    Spacer(Modifier.height(2.dp))
                    FloatingActionButton(
                        onClick = { onEvent(ToggleBoundary) },
                        modifier = Modifier.zIndex(1f).alpha(if (state.showBoundary) 1.0f else 0.5f)
                    ) {
                        Icon(RescueIcons.CropSquare, "Boundary")

                    }
                    Spacer(Modifier.height(2.dp))
                    FloatingActionButton(
                        onClick = { onEvent(TogglePath) },
                        modifier = Modifier.zIndex(1f)
                            .alpha(if (state.showPath) 1.0f else 0.5f)
                    ) {
                        Icon(RescueIcons.Route, "Path")
                    }
                }
            }
        }


    }

}

