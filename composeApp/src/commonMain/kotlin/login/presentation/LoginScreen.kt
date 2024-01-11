package login.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.gotrue.providers.Github
import io.github.jan.supabase.gotrue.providers.Google
import login.presentation.components.EmailEntryDialog
import login.presentation.components.SignUpDialog
import login.presentation.components.SignUpEnum
import org.koin.compose.koinInject

@OptIn(SupabaseExperimental::class)
@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    val supabase = koinInject<SupabaseClient>()

    val action = supabase.composeAuth.rememberSignInWithGoogle(fallback = { onEvent(LoginEvent.OnSignupGoogle) },
        onResult = { result -> //optional error handling
            println(result)
            when (result) {
                is NativeSignInResult.Error -> {
                }
                else -> {}
            }
        }
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!state.sessionChecked) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                return
            }
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Rounded.Android,
                contentDescription = "Logo"
            )
            Spacer(Modifier.height(20.dp))

            // Error message display
            state.errorLogin?.let { errorMessage ->
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
            }
            Button(onClick = {
                action.startFlow()
            }) {
                ProviderButtonContent(Google)
            }
            Button(onClick = { onEvent(LoginEvent.OnLoginGithub) }) {
                ProviderButtonContent(Github)
            }
            Button(onClick = { onEvent(LoginEvent.OnShowEmailDialog(true)) }) {
                Text(text = "Log In with Email")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.rememberLogin,
                    onCheckedChange = { onEvent(LoginEvent.OnRememberLoginClicked(it)) }
                )
                Text(
                    modifier = Modifier.clickable { onEvent(LoginEvent.OnRememberLoginClicked(!state.rememberLogin)) },
                    text = "Remember Login"
                )
            }
            Spacer(Modifier.height(20.dp))
            Divider()
            Spacer(Modifier.height(20.dp))
            OutlinedButton(onClick = { onEvent(LoginEvent.OnShowSignUpDialog(true)) }) {
                Text(text = "Sign Up")
            }

        }

        if (state.showSignUpDialog) {
            SignUpDialog(onSignUpSelected = { method ->
                when (method) {
                    SignUpEnum.Google -> action.startFlow()
                    SignUpEnum.GitHub -> onEvent(LoginEvent.OnSignupGithub)
                    SignUpEnum.Email -> onEvent(
                        LoginEvent.OnShowEmailDialog(
                            show = true,
                            fromSignUp = true
                        )
                    )

                    SignUpEnum.CANCEL -> onEvent(LoginEvent.OnShowSignUpDialog(false))
                }
                onEvent(LoginEvent.OnShowSignUpDialog(false))
            })
        }

        if (state.showEmailDialog) {
            EmailEntryDialog(
                email = state.email,
                password = state.password,
                onEvent = onEvent,
                onDismiss = { onEvent(LoginEvent.OnShowEmailDialog(false)) },
                onEmailEntered = { email, password ->
                    when (state.emailFromSignup) {
                        true -> onEvent(LoginEvent.OnSignupEmail(email, password))
                        false -> onEvent(LoginEvent.OnLoginEmail(email, password))
                    }
                    onEvent(LoginEvent.OnShowEmailDialog(false))
                }
            )
        }
    }
}