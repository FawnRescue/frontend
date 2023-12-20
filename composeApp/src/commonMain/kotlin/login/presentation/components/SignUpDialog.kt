package login.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.gotrue.providers.Github
import io.github.jan.supabase.gotrue.providers.Google

@OptIn(SupabaseExperimental::class)
@Composable
fun SignUpDialog(onSignUpSelected: (SignUpEnum) -> Unit) {
    Dialog(onDismissRequest = { onSignUpSelected(SignUpEnum.CANCEL) }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clip(RoundedCornerShape(10))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Sign Up", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onSignUpSelected(SignUpEnum.Google) }) {
                    ProviderButtonContent(Google, text = "Sign Up with Google")
                }
                Button(onClick = { onSignUpSelected(SignUpEnum.GitHub) }) {
                    ProviderButtonContent(Github, text = "Sign Up with Github")
                }
                Button(onClick = { onSignUpSelected(SignUpEnum.Email) }) {
                    Text("Sign Up with Email")
                }
            }
        }
    }
}


