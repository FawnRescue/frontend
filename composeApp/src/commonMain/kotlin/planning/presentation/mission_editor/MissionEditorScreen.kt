package planning.presentation.mission_editor


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import planning.presentation.components.flightdate_list.FlightDateList

@Composable
fun MissionEditorScreen(onEvent: (MissionEditorEvent) -> Unit, state: MissionEditorState) {
    Column {
        TextField(state.editedMission.description, onValueChange = {
            onEvent(MissionEditorEvent.UpdateMission(state.editedMission.copy(description = it)))
        })
        TextButton(onClick = { onEvent(MissionEditorEvent.SaveMission) }, enabled = state.selectedMission == null || state.selectedMission.description != state.editedMission.description) {
            if (state.selectedMission == null) Text("Save Description") else Text("Save new Description")
        }
        state.selectedMission?.let {
            TextButton(onClick = { onEvent(MissionEditorEvent.EditFlightPlan) }) {
                Text("Edit Flight Plan")
            }
            TextButton(onClick = { onEvent(MissionEditorEvent.AddFlightDate) }) {
                Text("Add Flight Date")
            }
            TextButton(onClick = { onEvent(MissionEditorEvent.ResetMission) }) {
                Text("Reset Description")
            }
        }
        TextButton(onClick = { onEvent(MissionEditorEvent.Cancel) }) {
            Text("Cancel")
        }

        FlightDateList(state.dates, onSelectDate = { onEvent(MissionEditorEvent.DateSelected(it)) })
    }

}

