package pilot

import DetectionImageFromByteArray
import ImageFromByteArray
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import hangar.domain.AircraftState.ARMED
import hangar.domain.AircraftState.IDLE
import hangar.domain.AircraftState.IN_FLIGHT
import hangar.domain.AircraftState.NOT_CONNECTED
import hangar.domain.AircraftStatus
import hangar.domain.Location
import hangar.presentation.components.BatteryIndicator
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pilot.PilotEvent.DetectionDeselected
import pilot.PilotEvent.DetectionSelected
import pilot.PilotEvent.SendCommand
import planning.presentation.flightplan_editor.GoogleMaps
import presentation.maps.LatLong
import presentation.maps.getCenter
import repository.domain.Commands.ARM
import repository.domain.Commands.CONTINUE
import repository.domain.Commands.DISARM
import repository.domain.Commands.ELAND
import repository.domain.Commands.KILL
import repository.domain.Commands.RTH
import repository.domain.Commands.TAKEOFF
import repository.domain.Detection
import repository.domain.InsertableCommand

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
            GoogleMaps(state.plan.boundary.getCenter(),
                onMapClick = {},
                onMarkerClick = {},
                listOf(),
                state.plan.checkpoints ?: listOf(),
                showBoundaryMarkers = false,
                showBoundary = false,
                showCheckpointMarkers = false,
                showPath = true,
                dronePosition = state.aircraftStatus.location?.toLatLong(),
                personPositions = state.helperLocations.values.toList(),
                detections = state.detections,
                onDetectionMarkerClick = { onEvent(DetectionSelected(it)) }
            )
        }
    }
}

// Data class for checklist items
data class ChecklistItem(
    val title: String,
    val loaded: Boolean,
    val contentDescriptionPrefix: String = "",
)

@Composable
fun DetectionDialog(
    detection: Detection,
    rgb: ByteArray?,
    thermal: ByteArray?,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column {
            if (rgb == null) {
                CircularProgressIndicator()
            } else {
                ImageFromByteArray(rgb)
            }
            if (thermal == null) {
                CircularProgressIndicator()
            } else {
                DetectionImageFromByteArray(thermal, detection)
            }
        }

    }
}

// Composable for a single checklist row
@Composable
fun ChecklistRow(item: ChecklistItem) {
    val loadedColor = Color.Green
    val unloadedColor = MaterialTheme.colorScheme.error

    Row {
        Icon(
            imageVector = if (item.loaded) Icons.Rounded.Check else Icons.Rounded.Close,
            contentDescription = "${item.contentDescriptionPrefix} ${if (item.loaded) "loaded" else "not loaded"}",
            modifier = Modifier.size(25.dp),
            tint = if (item.loaded) loadedColor else unloadedColor
        )
        Text(
            item.title,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (item.loaded) FontWeight.Bold else FontWeight.Normal)
        )
    }
}

// Main pre-flight checklist Composable
@Composable
fun PreFlightChecklist(state: PilotState) {
    val checklistItems = listOf(
        ChecklistItem("Flight Plan created?", state.plan != null, "plan"),
        ChecklistItem("Mission loaded?", state.mission != null, "mission"),
        ChecklistItem("Flight Date loaded?", state.date != null, "date"),
        ChecklistItem("Aircraft information loaded?", state.aircraft != null, "aircraft"),
        ChecklistItem("Aircraft connected?", state.aircraftStatus != null, "Aircraft"),
        ChecklistItem(
            "Aircraft ready?",
            state.aircraftStatus != null && state.aircraftStatus.state != NOT_CONNECTED,
            "Aircraft"
        )
    )

    Scaffold(topBar = {
        Text("Pre-Flight Checklist")
    }, modifier = Modifier.padding(10.dp)) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                checklistItems.forEach { item ->
                    ChecklistRow(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

enum class OSDRowType(val icon: ImageVector) {
    STATE(Icons.Default.Flight),
    LOCATION(Icons.Default.LocationOn),
    ALTITUDE(Icons.Default.Terrain),
    SATELLITES(Icons.Default.Satellite)
}

@Composable
fun DisplayRow(rowType: OSDRowType, value: String) {
    Row {
        Icon(rowType.icon, contentDescription = rowType.name)
        Text(" ${rowType.name}: $value")
    }
}

@Composable
fun OSD(status: AircraftStatus?) {
    status ?: run {
        println("no state for OSD")
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).zIndex(2f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            BatteryIndicator(
                batteryPercentage = status.battery?.remainingPercent ?: 0f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            DisplayRow(OSDRowType.STATE, status.state.name)
            DisplayRow(
                OSDRowType.LOCATION,
                "${status.location?.latitude}, ${status.location?.longitude}"
            )

            status.altitude?.let { DisplayRow(OSDRowType.ALTITUDE, "${it}m") }
            status.numSatellites?.let { DisplayRow(OSDRowType.SATELLITES, "$it") }
        }
    }
}

@Composable
fun Controls(
    status: AircraftStatus?,
    onArm: () -> Unit,
    onTakeoff: () -> Unit,
    onRTH: () -> Unit,
    onKill: () -> Unit,
    onELAND: () -> Unit,
    onContinue: () -> Unit,
    onDisarm: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(modifier = Modifier.padding(8.dp)) {
        if (status == null || status.state == NOT_CONNECTED) {
            Text("Aircraft not connected")
            return
        }
        Card(modifier = Modifier.fillMaxWidth().zIndex(2f)) {
            Text(modifier = Modifier.padding(start = 16.dp, top = 16.dp), text = "Hold to activate")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp)
            ) {
                items(
                    listOf(
                        "Arm" to onArm,
                        "Disarm" to onDisarm,
                        "Takeoff" to onTakeoff,
                        "Return to Home" to onRTH,
                        "Emergency Land" to onELAND,
                        "Kill" to onKill,
                        "Continue Mission" to onContinue,
                    )
                ) { (text, onClick) ->
                    val enabled = when (text) {
                        "Arm" -> status.state == IDLE
                        "Disarm" -> status.state == ARMED
                        "Takeoff" -> status.state == ARMED
                        "Return to Home", "Emergency Land", "Kill" -> status.state == IN_FLIGHT
                        "Continue Mission" -> status.state == ARMED || status.state == IN_FLIGHT
                        else -> true
                    }
                    Button(modifier = if (!enabled) Modifier else Modifier.onTouchHeld(
                        onTouchSuccess = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        }), onClick = { }, enabled = enabled
                    ) {
                        Text(text)
                    }
                }
            }
        }
    }
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