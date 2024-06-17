package pilot

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import hangar.domain.AircraftState.NOT_CONNECTED
import hangar.domain.Location
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pilot.PilotEvent.DetectionDeselected
import pilot.PilotEvent.DetectionSelected
import pilot.PilotEvent.SendCommand
import planning.presentation.flightplan_editor.GoogleMaps
import planning.presentation.flightplan_editor.GoogleMapsConfig
import planning.presentation.flightplan_editor.GoogleMapsData
import planning.presentation.flightplan_editor.GoogleMapsFunctions
import presentation.maps.LatLong
import presentation.maps.getCenter
import repository.domain.Commands.ARM
import repository.domain.Commands.CONTINUE
import repository.domain.Commands.DISARM
import repository.domain.Commands.ELAND
import repository.domain.Commands.KILL
import repository.domain.Commands.RTH
import repository.domain.Commands.TAKEOFF
import repository.domain.InsertableCommand
import kotlin.math.roundToInt

fun Location.toLatLong(): LatLong {
    return LatLong(this.latitude, this.longitude)
}

@Composable
fun PilotScreen(onEvent: (PilotEvent) -> Unit, state: PilotState) {
    if (state.loading) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        return
    }
    if (state.isPilot) {
        if (state.plan == null || state.date == null || state.mission == null || state.aircraft == null || state.aircraftStatus == null || state.aircraftStatus.state == NOT_CONNECTED) {
            return PreFlightChecklist(state)
        }
    } else {
        if (state.plan == null || state.date == null || state.mission == null || state.aircraftStatus == null || state.aircraftStatus.state == NOT_CONNECTED) {
            return PreFlightChecklist(state)
        }
    }

    if (state.selectedDetection != null) {
        DetectionDialog(
            state.selectedDetection,
            state.selectedDetectionRGBImageData,
            state.selectedDetectionThermalImageData,
            onDismiss = { onEvent(DetectionDeselected) })
    }


    Column {
        OSD(state.aircraftStatus)
        if (state.isPilot && state.aircraft != null) {
            val command = InsertableCommand(
                ARM, context = state.date.id, aircraft = state.aircraft.token
            )
            Controls(
                state.aircraftStatus,
                state.isExecutingCommand,
                onArm = {
                    onEvent(
                        SendCommand(
                            command.copy(command = ARM)
                        )
                    )
                },
                onContinue = { onEvent(SendCommand(command.copy(command = CONTINUE))) },
                onDisarm = { onEvent(SendCommand(command.copy(command = DISARM))) },
                onELAND = { onEvent(SendCommand(command.copy(command = ELAND))) },
                onKill = { onEvent(SendCommand(command.copy(command = KILL))) },
                onTakeoff = { onEvent(SendCommand(command.copy(command = TAKEOFF))) },
                onRTH = { onEvent(SendCommand(command.copy(command = RTH))) },
            )
        }
        Card {
            GoogleMaps(
                data = GoogleMapsData(
                    initialPosition = state.plan.boundary.getCenter(),
                    homePosition = state.aircraftStatus.homeLocation?.toLatLong(),
                    drone = state.aircraftStatus.location?.toLatLong(),
                    checkpoints = state.plan.checkpoints ?: listOf(),
                    personPositions = state.helperLocations.values.toList(),
                    detections = state.detections,
                    droneRotation = state.aircraftStatus.heading
                ),
                config = GoogleMapsConfig(
                    showBoundaryMarkers = false,
                    showBoundary = false,
                    showCheckpointMarkers = false,
                    showPath = true,
                    showDrone = true,
                    showPilot = true,
                    showHelper = true,
                    showDetections = true,
                    showHome = true
                ),
                functions = GoogleMapsFunctions(
                    onDetectionMarkerClick = { onEvent(DetectionSelected(it)) }
                ),
            )
        }
    }
}


fun Float.roundToDecimals(decimals: Int): Float {
    var dotAt = 1
    repeat(decimals) { dotAt *= 10 }
    val roundedValue = (this * dotAt).roundToInt()
    return (roundedValue / dotAt) + (roundedValue % dotAt).toFloat() / dotAt
}

fun Modifier.onTouchHeld(
    pollDelay: Long = 50,
    successTime: Long = 1000,
    onTouchHeld: (timeElapsed: Long) -> Unit = {},
    onTouchSuccess: () -> Unit = {},
    onTouchStop: (timeElapsed: Long) -> Unit = {},
) = composed {
    val scope = rememberCoroutineScope()
    var success = false
    pointerInput(onTouchHeld) {
        awaitEachGesture {
            val initialDown = awaitFirstDown(requireUnconsumed = false)
            val initialDownTime = getTimeMillis()
            val initialTouchHeldJob = scope.launch {
                while (initialDown.pressed) {
                    val timeElapsed = getTimeMillis() - initialDownTime
                    onTouchHeld(timeElapsed)
                    if (timeElapsed > successTime && !success) {
                        success = true
                        onTouchSuccess()
                    }
                    delay(pollDelay)
                }
            }
            waitForUpOrCancellation()
            onTouchStop(getTimeMillis() - initialDownTime)
            success = false
            initialTouchHeldJob.cancel()
        }
    }
}