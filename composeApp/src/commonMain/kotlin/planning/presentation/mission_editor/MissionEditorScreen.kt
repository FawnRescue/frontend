package planning.presentation.mission_editor


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.utils.RescueIcons
import planning.presentation.components.flightdate_list.FlightDateList
import planning.presentation.mission_editor.MissionEditorEvent.Cancel
import planning.presentation.mission_editor.MissionEditorEvent.DateSelected
import planning.presentation.mission_editor.MissionEditorEvent.EditFlightPlan
import planning.presentation.mission_editor.MissionEditorEvent.SaveMission
import planning.presentation.mission_editor.MissionEditorEvent.UpdateMission

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MissionEditorScreen(onEvent: (MissionEditorEvent) -> Unit, state: MissionEditorState) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Mission Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedButton(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(),
                onClick = { onEvent(Cancel) },
            ) {
                Icon(
                    RescueIcons.Close, "Close"
                )
            }
        }
        Column(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
        ) {
            if (state.editedMission != null) {
                OutlinedTextField(
                    enabled = state.editable,
                    value = state.editedMission.description,
                    onValueChange = { onEvent(UpdateMission(state.editedMission.copy(description = it))) },
                    label = { Text("Enter mission description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors()
                )

                Button(
                    onClick = { onEvent(SaveMission) }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            } else {
                TextField(
                    readOnly = true,
                    label = { Text("Description") },
                    value = state.selectedMission?.description ?: "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors()
                )
                if (state.editable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onEvent(MissionEditorEvent.EditMission) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Mission")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (state.editable) {
                state.selectedMission?.let {
                    Button(
                        onClick = { onEvent(EditFlightPlan) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            RescueIcons.AddLocation, "EditFlightPlan", Modifier.size(
                                24.dp
                            )
                        )
                        Text("Edit Flight Plan")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Scheduled Flight Dates:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(),
                    onClick = { onEvent(MissionEditorEvent.AddFlightDate) },
                ) {
                    Icon(
                        RescueIcons.Add, "EditFlightPlan"
                    )
                }
            }

            FlightDateList(
                state.dates,
                onSelectDate = if (state.editable) ({ onEvent(DateSelected(it)) }) else null
            )
        }
    }
}
