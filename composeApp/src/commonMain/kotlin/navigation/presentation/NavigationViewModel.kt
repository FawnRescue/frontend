package navigation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NavigationViewModel : ViewModel(), KoinComponent {
    private val navigator by inject<Navigator>()
    var selectedItem by mutableStateOf(NAV.entries.first())
        private set

    fun onEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.OnNavItemClicked -> {
                if (selectedItem == event.item) return
                navigator.navigate(event.item.path)
            }

            NavigationEvent.OnSuccessfulLogin -> {
                selectedItem = NAV.HOME
                navigator.navigate(
                    NAV.HOME.path,
                    NavOptions(popUpTo = PopUpTo.First(true))
                )
            }

            is NavigationEvent.OnRouteChange -> {
                selectedItem = event.item
            }
        }
    }
}
