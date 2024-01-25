package planning.presentation.flightplan_editor

import androidx.compose.runtime.Composable
import presentation.maps.LatLong

@Composable
expect fun GoogleMaps(
    currentPosition: LatLong,
    onMapClick: (LatLong) -> Unit,
    onMarkerClick: (LatLong) -> Unit,
    markers: List<LatLong>,
    checkpoints: List<LatLong>
)