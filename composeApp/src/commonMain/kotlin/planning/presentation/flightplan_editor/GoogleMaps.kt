package planning.presentation.flightplan_editor

import androidx.compose.runtime.Composable
import pilot.PersonLocation
import presentation.maps.LatLong

@Composable
expect fun GoogleMaps(
    currentPosition: LatLong,
    onMapClick: (LatLong) -> Unit,
    onMarkerClick: (LatLong) -> Unit,
    markers: List<LatLong>,
    checkpoints: List<LatLong>,
    showBoundaryMarkers: Boolean,
    showBoundary: Boolean,
    showCheckpointMarkers: Boolean,
    showPath: Boolean,
    dronePosition: LatLong? = null,
    pilotPosition: LatLong? = null,
    helperPositions: List<PersonLocation>? = null,
    detections: List<LatLong>? = null
)