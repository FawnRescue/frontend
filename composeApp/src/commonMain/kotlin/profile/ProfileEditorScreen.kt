package profile


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import profile.ProfileEditorEvent.*

@Composable
fun ProfileEditorScreen(onEvent: (ProfileEditorEvent) -> Unit, state: ProfileEditorState) {
    Column {
        TextField(
            label = { Text(text = "Your Name") },
            value = state.editedUser?.name ?: "",
            onValueChange = { name -> onEvent(NameChanged(name)) })
        Button(onClick = {
            onEvent(Save)
        }) {
            Text("Save")
        }
        Button(
            onClick = {
                onEvent(Cancel)
            }
        ) {
            Text("Cancel")
        }
    }
}

