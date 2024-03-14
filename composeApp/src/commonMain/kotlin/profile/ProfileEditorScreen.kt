package profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ProfileEditorScreen(onEvent: (ProfileEditorEvent) -> Unit, state: ProfileEditorState) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
        ) {
            Text(
                "Edit Profile",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.editedUser?.name ?: "",
                onValueChange = { name -> onEvent(ProfileEditorEvent.NameChanged(name)) },
                label = {
                    Text(
                        text = "Your Name"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onEvent(ProfileEditorEvent.Save) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp), // Ensures buttons are the same size
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.width(16.dp)) // Adds spacing between buttons

                Button(
                    onClick = { onEvent(ProfileEditorEvent.Cancel) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp), // Ensures buttons are the same size
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}