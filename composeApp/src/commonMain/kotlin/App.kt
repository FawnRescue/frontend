import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Github
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.AppTheme

@OptIn(ExperimentalResourceApi::class, DelicateCoroutinesApi::class)
@Composable
fun App() {
    KoinContext {
        AppTheme {
            Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                val client = koinInject<SupabaseClient>()
                var greetingText by remember { mutableStateOf("Hello World!") }
                var showImage by remember { mutableStateOf(false) }
                var text by remember { mutableStateOf("Disconnected") }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        greetingText = "Compose: ${Greeting().greet()}"
                        showImage = !showImage
                    }) {
                        Text(greetingText)
                    }
                    AnimatedVisibility(showImage) {
                        Image(
                            painterResource("compose-multiplatform.xml"),
                            null
                        )
                    }

                    Login(loginGithub = {
                        GlobalScope.launch {
                            val user = client.gotrue.loginWith(Github)
                            // Log user details if the signup is successful
                            println(client.gotrue.currentUserOrNull())
                            println("User signed up successfully: $user")
                        }
                    })
                    Button(onClick = {
                        GlobalScope.launch {
                            client.gotrue.logout()
                        }
                    },
                        content = { Text("Logout") }

                    )
                    Button(onClick = {
                        runBlocking {
                            text = client.gotrue.currentUserOrNull()?.email.toString()
                        }
                    },
                        content = { Text(text) }

                    )
                }
            }
        }
    }
}

@Composable
@OptIn(SupabaseExperimental::class)
fun Login(loginGithub: () -> Unit) {
    OutlinedButton(
        onClick = loginGithub,
        content = { ProviderButtonContent(Github) }
    )
}