package hangar.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NavigationEnum
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HangarViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()

    private val _state = MutableStateFlow(HangarState(emptyList()))
    val state = _state.asStateFlow()


    fun onEvent(event: HangarEvent) {
        when (event) {
            HangarEvent.AddAircraft -> navigator.navigate(NavigationEnum.HANGAR_DISCOVER.path)
        }
    }
}