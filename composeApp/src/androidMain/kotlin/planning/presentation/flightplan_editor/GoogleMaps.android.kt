package planning.presentation.flightplan_editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import hangar.domain.Location
import org.fawnrescue.project.R
import presentation.maps.LatLong
import presentation.maps.getCenter


fun LatLong.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun LatLng.toLatLong(): LatLong {
    return LatLong(this.latitude, this.longitude)
}

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
    println(checkpoints)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition.toLatLng(), 16f)
    }
    GoogleMap(modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            mapToolbarEnabled = false,
            zoomGesturesEnabled = true,
        ),
        properties = MapProperties(
            mapType = MapType.SATELLITE

        ),
        onMapClick = { onMapClick(it.toLatLong()) }) {
        if (showBoundaryMarkers) {
            markers.forEachIndexed { i, pos ->
                Marker(
                    state = MarkerState(position = pos.toLatLng()),
                    onClick = {
                        onMarkerClick(it.position.toLatLong())
                        true
                    },
                )
            }
        }
        if (showCheckpointMarkers) {
            checkpoints.forEachIndexed { i, pos ->
                Marker(state = MarkerState(position = pos.toLatLng()), alpha = 0.5f, title = "$i")
            }
        }
        if (markers.isNotEmpty() && showBoundary) {
            Polygon(
                points = markers.map(LatLong::toLatLng),
                fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
        if (checkpoints.isNotEmpty() && showPath) {
            Polyline(points = checkpoints.map(LatLong::toLatLng), color = Color.White, zIndex = 3f)
        }
        Marker(state = MarkerState(position = checkpoints.getCenter().toLatLng()), rotation = 180f)

        if (dronePosition != null) {
            Marker(
                state = MarkerState(position = dronePosition.toLatLng()),
                title = "Drone",
                icon = BitmapDescriptorFactory.fromResource(
                    R.drawable.drone
                ),
                anchor = Offset(0.5f, 0.5f)
            )
        }

        if (pilotPosition != null) {
            Marker(
                state = MarkerState(position = pilotPosition.toLatLng()),
                title = "Pilot",
            )
        }
    }
}