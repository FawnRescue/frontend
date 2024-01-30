package hangar.presentation.discover

import core.utils.randomAESKey
import core.utils.randomUUID
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.Aircraft
import hangar.domain.InsertableAircraft
import hangar.domain.InsertableAircraftSecret
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
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
                    val token = randomUUID()

                    val key = randomAESKey()

                    val connected = bluetoothClient.connectDrone(event.address, key, token)
                    if (connected) {
                        supabase.from("aircraft")
                            .insert(
                                InsertableAircraft(
                                    name = "${supabase.auth.currentUserOrNull()?.id}-Aircraft",
                                    token = token
                                )
                            )
                        supabase.from("aircraft_secret")
                            .insert(
                                InsertableAircraftSecret(
                                    key = key,
                                    token = token
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