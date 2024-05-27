package planning.presentation.flightplan_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import core.utils.RescueIcons
import org.fawnrescue.project.R
import pilot.PersonLocation
import pilot.RescuerRole
import presentation.maps.LatLong
import presentation.maps.getCenter
import repository.sortPolarCoordinates
import ui.FawnRescueTheme
import kotlin.random.Random


fun LatLong.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun LatLng.toLatLong(): LatLong {
    return LatLong(this.latitude, this.longitude)
}

@Preview
@Composable
fun Maps_preview() {

    fun getRandomLocation(center: LatLong, offset: Float): LatLong {
        return LatLong(
            center.latitude + (((Random.nextFloat() * 2) - 1) * offset),
            center.longitude + (((Random.nextFloat() * 2) - 1) * offset)
        )
    }

    val initialLocation = LatLong(51.5534005, 9.9746353)
    val droneLocation = getRandomLocation(initialLocation, 0.002f)
    val personLocations = listOf(
        PersonLocation(getRandomLocation(initialLocation, 0.002f), RescuerRole.RESCUER),
        PersonLocation(getRandomLocation(initialLocation, 0.002f), RescuerRole.RESCUER),
        PersonLocation(getRandomLocation(initialLocation, 0.002f), RescuerRole.RESCUER),
        PersonLocation(getRandomLocation(initialLocation, 0.002f), RescuerRole.PILOT),
    )
    val boundary = listOf(
        getRandomLocation(initialLocation, 0.002f),
        getRandomLocation(initialLocation, 0.002f),
        getRandomLocation(initialLocation, 0.002f),
        getRandomLocation(initialLocation, 0.002f),
    ).sortPolarCoordinates()
    val checkpoints = listOf(
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),
        getRandomLocation(initialLocation, 0.0005f),

    ).sortPolarCoordinates()

    FawnRescueTheme {
        GoogleMaps(
            data = GoogleMapsData(
                initialLocation,
                boundary = boundary,
                checkpoints = checkpoints,
                drone = droneLocation,
                personPositions = personLocations
            ),
            config = GoogleMapsConfig(
                showHome = true,
                showBoundary = true,
                showBoundaryMarkers = true,
                showCheckpointMarkers = true,
                showPath = true
            ),
            functions = GoogleMapsFunctions(onMapClick = { println("onMapClick") },
                onMarkerClick = { println("onMarkerClick") },
                onDetectionMarkerClick = { println("onDetectionMarkerClick") })
        )
    }
}

@Composable
actual fun GoogleMaps(
    config: GoogleMapsConfig,
    data: GoogleMapsData,
    functions: GoogleMapsFunctions,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(data.initialPosition.toLatLng(), 16f)
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
        onMapClick = { functions.onMapClick(it.toLatLong()) }) {
        if (config.showBoundaryMarkers) {
            data.boundary.forEachIndexed { i, pos ->
                MarkerComposable(
                    state = MarkerState(position = pos.toLatLng()),
                    onClick = {
                        functions.onMarkerClick(it.position.toLatLong())
                        true
                    },
                ) {
                    Icon(
                        imageVector = RescueIcons.LocationOn,
                        contentDescription = "Boundary",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Red
                    )
                }
            }
        }
        if (config.showCheckpointMarkers) {
            data.checkpoints.forEachIndexed { i, pos ->
                MarkerComposable(
                    state = MarkerState(position = pos.toLatLng()),
                    title = "Image Capture #$i",
                    anchor = Offset(0.5f, 0.5f)

                ) {
                    Icon(
                        imageVector = RescueIcons.PhotoCamera,
                        contentDescription = "DroneImageCapture",
                        modifier = Modifier.size(20.dp).background(Color.White, RoundedCornerShape(8.dp)),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        if (config.showBoundary && data.boundary.isNotEmpty()) {
            Polygon(
                points = data.boundary.map(LatLong::toLatLng),
                fillColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            )
        }
        if (config.showPath && data.checkpoints.isNotEmpty()) {
            Polyline(
                points = data.checkpoints.map(LatLong::toLatLng), color = Color.White, zIndex = 3f
            )
        }
        if (config.showHome) {
            MarkerComposable(
                state = MarkerState(
                    position = if (data.checkpoints.isNotEmpty()) data.checkpoints.getCenter()
                        .toLatLng() else data.initialPosition.toLatLng()
                ),
                title = "Home Point",
                anchor = Offset(0.5f, 0.5f)
            ) {
                Icon(
                    imageVector = RescueIcons.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(8.dp)),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        if (data.drone != null) {
            Marker(
                state = MarkerState(position = data.drone.toLatLng()),
                title = "Drone",
                icon = BitmapDescriptorFactory.fromResource(
                    R.drawable.drone
                ),
                anchor = Offset(0.5f, 0.5f)
            )
        }

        data.personPositions.map {
            MarkerComposable(
                state = MarkerState(position = it.position.toLatLng()),
                title = if (it.role == RescuerRole.RESCUER) "Helper" else "Pilot",
                anchor = Offset(0.5f, 0.5f)
            ) {
                Icon(
                    imageVector = RescueIcons.Person,
                    contentDescription = "Person",
                    modifier = Modifier.size(20.dp).background(Color.White, RoundedCornerShape(8.dp)),
                    tint = if (it.role == RescuerRole.RESCUER) Color.Black else Color.Red,
                )
            }
        }

        data.detections.map { detection ->
            Marker(
                state = MarkerState(position = detection.location.toLatLng()),
                onClick = {
                    functions.onDetectionMarkerClick(detection)
                    true
                },
                title = "Detection",
            )
        }
    }
}