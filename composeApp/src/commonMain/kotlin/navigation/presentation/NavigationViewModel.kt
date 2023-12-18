package navigation.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : ViewModel() {
    private val _state = MutableStateFlow(NavigationState())
    val state = _state.asStateFlow()
    fun onEvent(event: NavigationEvent) {

    }
}