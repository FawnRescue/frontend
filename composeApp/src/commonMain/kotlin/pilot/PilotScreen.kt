package pilot

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import hangar.domain.AircraftState.ARMED
import hangar.domain.AircraftState.IDLE
import hangar.domain.AircraftState.IN_FLIGHT
import hangar.domain.AircraftState.NOT_CONNECTED
import hangar.domain.AircraftStatus
import hangar.presentation.components.BatteryIndicator
import planning.presentation.flightplan_editor.GoogleMaps
import presentation.maps.getCenter
import repository.domain.Commands
import repository.domain.FlightDateId
import repository.domain.InsertableCommand

@Composable
fun PilotScreen(onEvent: (PilotEvent) -> Unit, state: PilotState) {
    if (state.loading) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        return
    }
    if (state.plan == null || state.date == null || state.mission == null || state.aircraft == null || state.aircraftStatus == null || state.aircraftStatus.state == NOT_CONNECTED) {
        return PreFlightChecklist(state)
    }
    val command = InsertableCommand(
        Commands.ARM,
        // FlightDateId(state.date.id),
        context = "",
        aircraft = state.aircraft.token
    )

    Column {
        OSD(state.aircraftStatus)
        Controls(
            state.aircraftStatus,
            onArm = {
                onEvent(
                    PilotEvent.SendCommand(
                        command.copy(command = Commands.ARM)
                    )
                )
            },
            onContinue = { onEvent(PilotEvent.SendCommand(command.copy(command = Commands.CONTINUE))) },
            onDisarm = { onEvent(PilotEvent.SendCommand(command.copy(command = Commands.DISARM))) },
            onELAND = { onEvent(PilotEvent.SendCommand(command.copy(command = Commands.ELAND))) },
            onKill = { onEvent(PilotEvent.SendCommand(command.copy(command = Commands.KILL))) },
            onLoiter = { onEvent(PilotEvent.SendCommand(command.copy(command = Commands.LOITER))) },
            onRTH = { onEvent(PilotEvent.SendCommand(command.copy(command = Commands.RTH))) },
        )
        Card{
            GoogleMaps(
                state.plan.boundary.getCenter(),
                onMapClick = {
                },
                onMarkerClick = {
                },
                listOf(),
                state.plan.checkpoints ?: listOf(),
                showBoundaryMarkers = false,
                showBoundary = false,
                showCheckpointMarkers = false,
                showPath = true,
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

@Composable
fun OSD(status: AircraftStatus?) {
    if (status == null) {
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
                batteryPercentage = status.battery?.remainingPercent,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(Icons.Default.Flight, contentDescription = "State")
                Text(" State: ${status.state}")
            }
            Row {
                Icon(
                    Icons.Default.LocationOn, contentDescription = "Location"
                )
                Text(" Location: ${status.location}")
            }
        }
    }
}

@Composable
fun Controls(
    status: AircraftStatus?,
    onArm: () -> Unit,
    onLoiter: () -> Unit,
    onRTH: () -> Unit,
    onKill: () -> Unit,
    onELAND: () -> Unit,
    onContinue: () -> Unit,
    onDisarm: () -> Unit,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        if (status == null || status.state == NOT_CONNECTED) {
            Text("Aircraft not connected")
            return
        }

        Card(modifier = Modifier.fillMaxWidth().zIndex(2f)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(listOf(
                    "Arm" to onArm,
                    "Disarm" to onDisarm,
                    "Loiter" to onLoiter,
                    "Return to Home" to onRTH,
                    "Emergency Land" to onELAND,
                    "Kill" to onKill,
                    "Continue Mission" to onContinue,
                )) { (text, onClick) ->
                    Button(
                        onClick = { onClick() },
                        enabled = when (text) {
                            "Arm" -> status.state == IDLE
                            "Disarm" -> status.state == ARMED || status.state == IN_FLIGHT
                            "Loiter", "Return to Home", "Emergency Land", "Kill" -> status.state == IN_FLIGHT
                            "Continue Mission" -> status.state == ARMED
                            else -> true
                        }
                    ) {
                        Text(text)
                    }
                }
            }
        }
    }
}

