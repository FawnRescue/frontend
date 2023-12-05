import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Github
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class, SupabaseExperimental::class, DelicateCoroutinesApi::class)
@Composable
fun App() {
    MaterialTheme {
        val client by remember { mutableStateOf(getClient()) }

        var greetingText by remember { mutableStateOf("Hello World!") }
        var showImage by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf("Disconnected") }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
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
                runBlocking {
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

@Composable
@OptIn(SupabaseExperimental::class)
fun Login(loginGithub: () -> Unit) {
    OutlinedButton(
        onClick = loginGithub,
        content = { ProviderButtonContent(Github) }
    )
}

fun getClient(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = "https://irvsopidchmqfxbdpxqt.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlydnNvcGlkY2htcWZ4YmRweHF0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDE3MDI4NDgsImV4cCI6MjAxNzI3ODg0OH0.oaKgHBwqw5WsYhM1_nYNJKGyidmEkIO6GaqjEWtVHI8"
    ) {
        install(Postgrest)
        // TODO figure out the correct gotrue config this is sketchy
        install(GoTrue) {
            scheme = "app"
            host = "org.fawnrescue.project"
        }
        install(ComposeAuth)
    }
}
