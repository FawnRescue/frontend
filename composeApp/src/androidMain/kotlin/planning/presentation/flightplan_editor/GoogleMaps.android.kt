package planning.presentation.flightplan_editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
actual fun GoogleMaps() {
    val testLocation = LatLng(51.5534005,9.9746353)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(testLocation, 50f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            mapToolbarEnabled = false
        )
    ) {
        Marker(
            state = MarkerState(position = testLocation),
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }

}