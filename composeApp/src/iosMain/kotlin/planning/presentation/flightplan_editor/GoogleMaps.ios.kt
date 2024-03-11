package planning.presentation.flightplan_editor

import androidx.compose.runtime.Composable
import presentation.maps.LatLong

@Composable
actual fun GoogleMaps(
    currentPosition: LatLong,
    onMapClick: (LatLong) -> Unit,
    onMarkerClick: (LatLong) -> Unit,
    markers: List<LatLong>,
    checkpoints: List<LatLong>,
    showBoundaryMarkers: Boolean,
    showBoundary: Boolean,
    showCheckpointMarkers: Boolean,
    showPath: Boolean,
    dronePosition: LatLong?,
    pilotPosition: LatLong?,
    helperPositions: List<LatLong>?,
    detections: List<LatLong>?
) {
}