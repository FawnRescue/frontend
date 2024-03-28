package planning.presentation.flightdate_viewer

import presentation.maps.LatLong
import repository.domain.Detection
import repository.domain.FlightPlan
import repository.domain.Mission

data class FlightDateViewerState(
    val boundary: List<LatLong> = listOf(),
    val checkpoints: List<LatLong>? = listOf(),
    val selectedMission: Mission? = null,
    val selectedFlightPlan: FlightPlan? = null,
    val showBoundaryMarkers: Boolean = true,
    val showBoundary: Boolean = true,
    val showCheckpointMarkers: Boolean = false,
    val showDetectionMarkers: Boolean = true,
    val showPath: Boolean = true,
    val planLoading: Boolean = true,
    val selectedDetection: Detection? = null,
    val selectedDetectionRGBImageData: ByteArray? = null,
    val selectedDetectionThermalImageData: ByteArray? = null,
    val detections: List<Detection> = emptyList(),
)