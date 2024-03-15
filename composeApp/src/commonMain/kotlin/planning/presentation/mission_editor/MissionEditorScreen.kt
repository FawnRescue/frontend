package planning.presentation.mission_editor


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Mission Details",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("Description:", style = MaterialTheme.typography.bodyLarge)
        OutlinedTextField(
            enabled = state.editable,
            value = state.editedMission.description,
            onValueChange = { onEvent(UpdateMission(state.editedMission.copy(description = it))) },
            label = { Text("Enter mission description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (state.editable) {
            Button(
                onClick = { onEvent(SaveMission) },
                enabled = state.selectedMission == null || state.selectedMission.description != state.editedMission.description,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.selectedMission == null) Text("Save Description") else Text("Save New Description")
            }
            Spacer(modifier = Modifier.height(8.dp))

            state.selectedMission?.let {
                Text(
                    "Flight Plan Options:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(onClick = { onEvent(ResetMission) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Reset Description")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onEvent(EditFlightPlan) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Edit Flight Plan")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onEvent(AddFlightDate) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Add Flight Date")
                }


                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(onClick = { onEvent(Cancel) }, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Scheduled Flight Dates:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        FlightDateList(
            state.dates,
            onSelectDate = if (state.editable) ({ onEvent(DateSelected(it)) }) else null
        )
    }
}
