package login.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ui.LocalAuthState
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.compose.auth.ui.password.PasswordRule
import io.github.jan.supabase.compose.auth.ui.password.rememberPasswordRuleList
import login.presentation.LoginEvent

@OptIn(SupabaseExperimental::class, ExperimentalMaterial3Api::class)
@Composable
fun EmailEntryDialog(
    email: String,
    password: String,
    onEvent: (LoginEvent) -> Unit,
    onDismiss: () -> Unit,
    onEmailEntered: (String, String) -> Unit
) {
    val state = LocalAuthState.current
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(10))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Enter Your Email", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedEmailField(
                    value = email,
                    onValueChange = { onEvent(LoginEvent.OnEmailChange(it)) },
                    label = { Text("Email") },
                    mandatory = true
                )
                OutlinedPasswordField(
                    value = password,
                    onValueChange = { onEvent(LoginEvent.OnPasswordChange(it)) },
                    label = { Text("Password") },
                    rules = rememberPasswordRuleList(
                        PasswordRule.minLength(8),
                        PasswordRule.containsSpecialCharacter(),
                        PasswordRule.containsDigit(),
                        PasswordRule.containsLowercase(),
                        PasswordRule.containsUppercase()
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(enabled = state.validForm, onClick = { onEmailEntered(email, password) }) {
                    Text("Submit")
                }
            }
        }
    }
}
