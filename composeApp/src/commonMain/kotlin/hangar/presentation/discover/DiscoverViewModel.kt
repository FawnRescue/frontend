package hangar.presentation.discover

import core.utils.randomUUID
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.InsertableAircraft
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DiscoverViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val bluetoothClient: BluetoothClient by inject<BluetoothClient>()

    private val _state = MutableStateFlow(DiscoverState(emptyList()))
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            bluetoothClient.scanningFlow().collect { isScanning ->
                _state.update { it.copy(isScanning = isScanning) }
            }
        }

        viewModelScope.launch {
            bluetoothClient.percentTransmitted().collect { percent ->
                _state.update { it.copy(percentTransmitted = percent) }
            }
        }
    }

    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.OnScanDevices -> {
                bluetoothClient.startScan()
                viewModelScope.launch {
                    bluetoothClient.getDrones().collect { devices ->
                        _state.update { it.copy(discoveredDevices = devices) }
                    }
                }
            }

            DiscoverEvent.OnCancelDiscovery -> {
                bluetoothClient.stopScan()
                navigator.goBack()
            }

            is DiscoverEvent.OnAddDrone -> {
                viewModelScope.launch {
                    // Generate a random UUID for the token
                    val currentSession = supabase.auth.currentSessionOrNull() ?: return@launch

                    val accessToken = currentSession.accessToken

                    val refreshToken = currentSession.refreshToken

                    val droneID = randomUUID()

                    val connected =
                        bluetoothClient.connectDrone(event.address, refreshToken, accessToken, droneID)
                    if (connected) {
                        supabase.from("aircraft")
                            .insert(
                                InsertableAircraft(
                                    name = "${supabase.auth.currentUserOrNull()?.id}-Aircraft",
                                    token = droneID
                                )
                            )
                    }
                    bluetoothClient.stopScan()
                    navigator.goBack()
                    println("Drone connected: $connected")
                }
            }
        }
    }
}