package hangar.presentation.discover

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.Aircraft
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NavigationEnum
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DiscoverViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val bluetoothServer: BluetoothServer by inject<BluetoothServer>()

    private val _state = MutableStateFlow(DiscoverState(emptyList()))
    val state = _state.asStateFlow()


    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.OnScanDevices -> {
                bluetoothServer.startServer()
                _state.update { it.copy(discoveredDevices = listOf("Drone1", "Drone2")) }
            }

            DiscoverEvent.OnCancelDiscovery -> {
                bluetoothServer.stopServer()
                navigator.goBack()
            }

            is DiscoverEvent.OnAddDrone -> TODO()
        }
    }
}