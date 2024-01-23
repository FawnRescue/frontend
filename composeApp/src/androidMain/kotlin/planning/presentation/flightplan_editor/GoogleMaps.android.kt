package planning.presentation.flightplan_editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import presentation.maps.LatLong
import presentation.maps.getCenter
import kotlin.math.atan2


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
) {
    println(checkpoints)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition.toLatLng(), 16f)
    }
    var showBoundaryMarkers by remember { mutableStateOf(true) }
    var showBoundary by remember { mutableStateOf(true) }
    var showCheckpointMarkers by remember { mutableStateOf(true) }
    var showPath by remember { mutableStateOf(true) }


    var selectedMarkerIndex by remember {
        mutableIntStateOf(0)
    }
    FloatingActionButton(
        onClick = { showBoundaryMarkers = !showBoundaryMarkers },
        modifier = Modifier
            .offset(100.dp)
            .zIndex(1f)
            .alpha(if (showBoundaryMarkers) 1.0f else 0.5f)
    ) {
        Text("BMarkers")
    }
    FloatingActionButton(
        onClick = { showCheckpointMarkers = !showCheckpointMarkers },
        modifier = Modifier
            .offset(160.dp)
            .zIndex(1f)
            .alpha(if (showCheckpointMarkers) 1.0f else 0.5f)
    ) {
        Text("CMarkers")
    }
    FloatingActionButton(
        onClick = { showBoundary = !showBoundary },
        modifier = Modifier
            .offset(220.dp)
            .zIndex(1f)
            .alpha(if (showBoundary) 1.0f else 0.5f)
    ) {
        Text("Boundary")
    }
    FloatingActionButton(
        onClick = { showPath = !showPath },
        modifier = Modifier
            .offset(280.dp)
            .zIndex(1f)
            .alpha(if (showPath) 1.0f else 0.5f)
    ) {
        Text("Path")
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
            checkpoints.forEach { pos ->
                Marker(state = MarkerState(position = pos.toLatLng()), alpha = 0.5f)
            }
        }
        if (markers.isNotEmpty() && showBoundary) {
            Polygon(points = markers.map(LatLong::toLatLng))
        }
        if (checkpoints.isNotEmpty() && showPath) {
            Polyline(points = checkpoints.map(LatLong::toLatLng), color= Color.White, zIndex = 3f)
        }
    }
}