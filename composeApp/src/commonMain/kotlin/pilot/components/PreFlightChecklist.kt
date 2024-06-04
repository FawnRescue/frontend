package pilot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import core.utils.RescueIcons
import hangar.domain.AircraftState

// Data class for checklist items
data class ChecklistItem(
    val title: String,
    val loaded: Boolean,
    val contentDescriptionPrefix: String = "",
)

// Composable for a single checklist row
@Composable
fun ChecklistRow(item: ChecklistItem) {
    val loadedColor = MaterialTheme.colorScheme.primary
    val unloadedColor = MaterialTheme.colorScheme.error

    Row {
        Icon(
            imageVector = if (item.loaded) RescueIcons.Check else RescueIcons.Close,
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
            state.aircraftStatus != null && state.aircraftStatus.state != AircraftState.NOT_CONNECTED,
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