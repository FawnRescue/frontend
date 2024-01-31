package hangar.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.Aircraft
import hangar.domain.DroneStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NavigationEnum
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HangarViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private var channel: RealtimeChannel? = null


    private val _state = MutableStateFlow(HangarState(null, null, null))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {

                _state.update {
                    it.copy(
                        aircrafts = supabase.from("aircraft")
                            .select { filter { eq("deleted", false) } }
                            .decodeList<Aircraft>()
                    )
                }
            } catch (_: HttpRequestException) {
                // This needs to be caught. Else the app crashes when leaving this screen before the result is there
            }
        }
    }


    fun onEvent(event: HangarEvent) {
        when (event) {
            HangarEvent.AddAircraft -> navigator.navigate(NavigationEnum.HANGAR_DISCOVER.path)
            HangarEvent.OnDismissDialog -> {
                viewModelScope.launch {
                    supabase.realtime.removeChannel(channel!!)
                }
                _state.update { it.copy(selectedAircraft = null, droneStatus = null) }
            }

            is HangarEvent.OnSelectAircraft -> viewModelScope.launch { selectAircraft(event.aircraft) }
            HangarEvent.OnDeleteAircraft -> {
                viewModelScope.launch {
                    supabase.from("aircraft").update({ set("deleted", true) }) {
                        filter {
                            eq("token", _state.value.selectedAircraft!!.token)
                        }
                    }
                    _state.update {
                        it.copy(
                            selectedAircraft = null,
                            aircrafts = supabase.from("aircraft")
                                .select { filter { eq("deleted", false) } }
                                .decodeList<Aircraft>()
                        )
                    }
                }
            }
        }

    }

    private suspend fun selectAircraft(aircraft: Aircraft) {
        _state.update { it.copy(selectedAircraft = aircraft) }
        println(aircraft.token)
        channel = supabase.channel(aircraft.token)
        val broadcastFlow = channel!!.broadcastFlow<DroneStatus>(event = "event")
        viewModelScope.launch {
            broadcastFlow.collect { status ->
                _state.update { it.copy(droneStatus = status) }
            }
        }
        println("Subscribing")
        channel!!.subscribe(blockUntilSubscribed = true)
        println("Subscribed")
    }
}