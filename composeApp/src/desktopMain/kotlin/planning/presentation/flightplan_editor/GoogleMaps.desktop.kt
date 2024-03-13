package planning.presentation.flightplan_editor

import androidx.compose.runtime.Composable
import pilot.PersonLocation
import presentation.maps.LatLong
import repository.domain.Detection


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
    personPositions: List<PersonLocation>?,
    detections: List<Detection>?,
    onDetectionMarkerClick: (Detection) -> Unit
) {
}