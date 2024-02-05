package planning.presentation.mission_editor


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import planning.presentation.components.flightdate_list.FlightDateList
import planning.presentation.mission_editor.MissionEditorEvent.AddFlightDate
import planning.presentation.mission_editor.MissionEditorEvent.Cancel
import planning.presentation.mission_editor.MissionEditorEvent.DateSelected
import planning.presentation.mission_editor.MissionEditorEvent.EditFlightPlan
import planning.presentation.mission_editor.MissionEditorEvent.ResetMission
import planning.presentation.mission_editor.MissionEditorEvent.SaveMission
import planning.presentation.mission_editor.MissionEditorEvent.UpdateMission

@Composable
fun MissionEditorScreen(onEvent: (MissionEditorEvent) -> Unit, state: MissionEditorState) {
    Column {
        TextField(state.editedMission.description, onValueChange = {
            onEvent(UpdateMission(state.editedMission.copy(description = it)))
        })
        TextButton(onClick = { onEvent(SaveMission) }, enabled = state.selectedMission == null || state.selectedMission.description != state.editedMission.description) {
            if (state.selectedMission == null) Text("Save Description") else Text("Save new Description")
        }
        state.selectedMission?.let {
            TextButton(onClick = { onEvent(EditFlightPlan) }) {
                Text("Edit Flight Plan")
            }
            TextButton(onClick = { onEvent(AddFlightDate) }) {
                Text("Add Flight Date")
            }
            TextButton(onClick = { onEvent(ResetMission) }) {
                Text("Reset Description")
            }
        }
        TextButton(onClick = { onEvent(Cancel) }) {
            Text("Cancel")
        }

        FlightDateList(state.dates, onSelectDate = { onEvent(DateSelected(it)) })
    }

}

