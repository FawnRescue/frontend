package planning.presentation.mission_editor


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import planning.presentation.mission_editor.MissionEditorEvent
import planning.presentation.mission_editor.MissionEditorState

@Composable
fun MissionEditorScreen(onEvent: (MissionEditorEvent) -> Unit, state: MissionEditorState) {
    Column {
        TextField(state.editedMission.description, onValueChange = {
            onEvent(MissionEditorEvent.UpdateMission(state.editedMission.copy(description = it)))
        })
        TextButton(onClick = { onEvent(MissionEditorEvent.SaveMission) }) {
            if (state.selectedMission == null) Text("Save mission") else Text("Edit mission")
        }
        state.selectedMission?.let {
            TextButton(onClick = { onEvent(MissionEditorEvent.ResetMission) }) {
                Text("Reset mission")
            }
        }
        TextButton(onClick = { onEvent(MissionEditorEvent.Cancel) }) {
            Text("Cancel")
        }
    }

}

