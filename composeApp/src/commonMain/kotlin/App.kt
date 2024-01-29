import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NavigationEnum
import navigation.presentation.NavigationEvent
import navigation.presentation.NavigationScreen
import navigation.presentation.NavigationViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.FawnRescueTheme

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun App() {
    PreComposeApp {
        KoinContext {
            FawnRescueTheme {

                val viewModel = getViewModel(
                    key = "nav-screen",
                    factory = viewModelFactory {
                        NavigationViewModel()
                    }
                )
                // Update selectedItem based on current nav route
                val navigator = koinInject<Navigator>()
                LaunchedEffect(Unit) {
                    navigator.currentEntry.collect { currentRoute ->
                        val correspondingItem =
                            NavigationEnum.entries.find {
                                it.path == (currentRoute?.path)
                            }
                        if (correspondingItem != null && correspondingItem != viewModel.selectedItem) {
                            viewModel.onEvent(NavigationEvent.OnRouteChange(correspondingItem))
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationScreen(
                        selectedItem = viewModel.selectedItem,
                        onEvent = viewModel::onEvent
                    )

                }
            }
        }
    }
}