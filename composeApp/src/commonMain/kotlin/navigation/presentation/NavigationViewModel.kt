package navigation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NavigationState())
    val state = _state.asStateFlow()
    var selectedItem by mutableStateOf(NavigationEnum.entries.first())
        private set

    init {
        observeNavigationChanges()
    }

    private fun observeNavigationChanges() {
        viewModelScope.launch {
            _state.value.navigator.currentEntry.collect { currentRoute ->
                updateSelectedItemBasedOnRoute(currentRoute)
            }

        }
    }

    //TODO this solves that the view model doesn't get cleared. Little bit sketchy
    override fun onCleared() {}

    private fun updateSelectedItemBasedOnRoute(currentRoute: BackStackEntry?) {
        // Determine the corresponding NavigationEnum entry based on the currentRoute.
        // This logic depends on how your routes are structured.
        val correspondingItem =
            NavigationEnum.entries.find {
                it.path == (currentRoute?.path)
            }
        if (correspondingItem != null && correspondingItem != selectedItem) {
            selectedItem = correspondingItem
        }
    }

    fun onEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.OnNavItemClicked -> {
                if (selectedItem == event.item) return
                selectedItem = event.item
                _state.value.navigator.navigate(event.item.path)
            }

            NavigationEvent.OnSuccessfulLogin -> {
                selectedItem = NavigationEnum.HOME
                _state.value.navigator.navigate(
                    NavigationEnum.HOME.path,
                    NavOptions(popUpTo = PopUpTo.First(true))
                )
            }
        }
    }
}
