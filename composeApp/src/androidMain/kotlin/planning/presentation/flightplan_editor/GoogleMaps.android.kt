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
    markers: List<LatLong>
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition.toLatLng(), 16f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            mapToolbarEnabled = false,
            zoomGesturesEnabled = true,
        ),
        properties = MapProperties(
            mapType = MapType.SATELLITE

        ),
        onMapClick = { onMapClick(it.toLatLong()) }
    ) {
        markers.forEach {
            Marker(
                state = MarkerState(position = it.toLatLng()),
            )
        }
        if (markers.isNotEmpty()) {
            Polygon(points = markers.map(LatLong::toLatLng))
        }
    }

}