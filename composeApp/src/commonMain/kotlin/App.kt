import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import friends.presentation.FriendListScreen
import friends.presentation.FriendListViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Github
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.tlaster.precompose.PreComposeApp
import navigation.presentation.NavigationEnum
import navigation.presentation.NavigationEvent
import navigation.presentation.NavigationScreen
import navigation.presentation.NavigationViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.FawnRescueTheme

@OptIn(ExperimentalResourceApi::class, DelicateCoroutinesApi::class)
@Composable
fun App() {
    FawnRescueTheme {
        KoinContext {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                val client = koinInject<SupabaseClient>()
                var greetingText by remember { mutableStateOf("Hello World!") }
                var showImage by remember { mutableStateOf(false) }
                var text by remember { mutableStateOf("Disconnected") }
                val scope = rememberCoroutineScope()
                val viewModel = getViewModel(
                    key = "nav-screen",
                    factory = viewModelFactory {
                        NavigationViewModel()
                    }
                )
                scope.launch {
                    client.gotrue.sessionStatus.collect {
                        println(it.toString())
                        if (it.toString() == "NotAuthenticated") return@collect
                        viewModel.onEvent(NavigationEvent.OnNavItemClicked(NavigationEnum.HOME))
                    }
                }
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
fun AppFriend() {
    KoinContext {
        FawnRescueTheme {
            val viewModel = getViewModel(
                key = "friend-list-screen",
                factory = viewModelFactory {
                    FriendListViewModel()
                }
            )
            val state by viewModel.state.collectAsState()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                FriendListScreen(
                    state = state,
                    newFriend = viewModel.newFriend,
                    onEvent = viewModel::onEvent
                )

            }
        }
    }
}

@Composable
fun AppNav() {
    PreComposeApp {
        KoinContext {
            FawnRescueTheme {
                val viewModel = getViewModel(
                    key = "nav-screen",
                    factory = viewModelFactory {
                        NavigationViewModel()
                    }
                )
                val state by viewModel.state.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationScreen(
                        state = state,
                        selectedItem = viewModel.selectedItem,
                        onEvent = viewModel::onEvent
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