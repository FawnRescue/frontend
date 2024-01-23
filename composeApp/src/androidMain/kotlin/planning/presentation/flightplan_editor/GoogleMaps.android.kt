package planning.presentation.flightplan_editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import presentation.maps.LatLong
import presentation.maps.getCenter
import kotlin.math.atan2


fun LatLong.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun LatLng.toLatLong(): LatLong {
    return LatLong(this.latitude, this.longitude)
}

fun sortPolarCoordinates(coordinates: List<LatLong>): List<LatLong> {
    val centroid = coordinates.getCenter()
    return coordinates.sortedWith(compareBy { atan2(it.latitude-centroid.latitude, it.longitude-centroid.longitude) })
}

@Composable
actual fun GoogleMaps(
    currentPosition: LatLong,
    onMapClick: (LatLong) -> Unit,
    onMarkerClick: (LatLong) -> Unit,
    markers: List<LatLong>,
) {
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
        markers.forEach { pos ->
            Marker(state = MarkerState(position = pos.toLatLng()), onClick = {
                onMarkerClick(it.position.toLatLong())
                true
            })
        }
        if (markers.isNotEmpty()) {
            Polygon(points = sortPolarCoordinates(markers).map(LatLong::toLatLng), geodesic = true)
        }
    }

}