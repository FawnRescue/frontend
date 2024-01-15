package hangar.presentation.discover

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DiscoverViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()

    private val _state = MutableStateFlow(DiscoverState(emptyList()))
    val state = _state.asStateFlow()


    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.OnScanDevices -> TODO()
        }
    }
}