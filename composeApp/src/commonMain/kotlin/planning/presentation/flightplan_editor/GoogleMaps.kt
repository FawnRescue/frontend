package planning.presentation.flightplan_editor

import androidx.compose.runtime.Composable
import pilot.PersonLocation
import presentation.maps.LatLong
import repository.domain.Detection

@Composable
expect fun GoogleMaps(
    config: GoogleMapsConfig,
    data: GoogleMapsData,
    functions: GoogleMapsFunctions,
    )

data class GoogleMapsFunctions(
    val onMapClick: (LatLong) -> Unit = {},
    val onMarkerClick: (LatLong) -> Unit = {},
    val onDetectionMarkerClick: (Detection) -> Unit = {},
)

data class GoogleMapsData(
    val initialPosition: LatLong,
    val drone: LatLong? = null,
    val boundary: List<LatLong> = listOf(),
    val checkpoints: List<LatLong> = listOf(),
    val personPositions: List<PersonLocation> = listOf(),
    val detections: List<Detection> = listOf(),
)

data class GoogleMapsConfig(
    val showBoundaryMarkers: Boolean = false,
    val showBoundary: Boolean = false,
    val showCheckpointMarkers: Boolean = false,
    val showPath: Boolean = false,
    val showHome: Boolean = false,
    val showDrone: Boolean = false,
    val showHelper: Boolean = false,
    val showPilot: Boolean = false,
    val showDetections: Boolean = false,
)