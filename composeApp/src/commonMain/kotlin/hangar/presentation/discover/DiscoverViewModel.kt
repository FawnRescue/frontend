package hangar.presentation.discover

import core.utils.randomUUID
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.OTP
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

    private val _state = MutableStateFlow(DiscoverState())
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

            DiscoverEvent.OnAddDrone -> {
                viewModelScope.launch {
                    // Generate a random UUID for the token

                    if (_state.value.otp.length != 6) {
                        return@launch
                    }
                    val email = supabase.auth.currentUserOrNull()?.email ?: return@launch

                    val connected =
                        bluetoothClient.connectDrone(
                            _state.value.selectedDrone!!,
                            email,
                            _state.value.otp,
                            randomUUID()
                        )
                    if (connected) {
                        bluetoothClient.stopScan()
                        navigator.goBack()
                        _state.update { it.copy(otp = "", selectedDrone = null) }
                    }
                }
            }

            is DiscoverEvent.OnSelectDrone -> {
                _state.update { it.copy(selectedDrone = event.address) }
                viewModelScope.launch {
                    try {
                        supabase.auth.signInWith(OTP) {
                            createUser = false
                            email = supabase.auth.currentUserOrNull()?.email
                        }
                    } catch (e: UnknownRestException) {
                        e.printStackTrace()
                        // TODO: add retry after 60 sec toast
                        _state.update { it.copy(selectedDrone = null) }
                    }
                }
            }

            is DiscoverEvent.OnChangeOTP -> {
                _state.update { it.copy(otp = event.otp) }
            }

            DiscoverEvent.OnCancelAddDrone -> {
                _state.update { it.copy(otp = "", selectedDrone = null) }
            }
        }
    }
}