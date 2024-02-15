package hangar.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.DroneStatus
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.HangarRepo
import repository.domain.Aircraft
import repository.domain.UserId

class HangarViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private var channel: RealtimeChannel? = null
    private val hangarRepo by inject<HangarRepo>()


    private val _state = MutableStateFlow(HangarState(null, null, null))
    val state = _state.asStateFlow()

    init {
        loadAircrafts()
    }

    private fun loadAircrafts() {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return
        val userId = UserId(authId)
        viewModelScope.launch {
            hangarRepo.getAircrafts(userId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> _state.update { it.copy(aircrafts = response.value, loading = false) }
                    is StoreReadResponse.Error.Exception -> Napier.e(
                        "Aircraft loading error",
                        response.error
                    )

                    is StoreReadResponse.Error.Message -> Napier.e(response.message)
                    is StoreReadResponse.Loading -> _state.update { it.copy(loading = true) }
                    is StoreReadResponse.NoNewData -> _state.update { it.copy(loading = false) }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    StoreReadResponse.Initial -> TODO()
                }
            }
        }
    }


    fun onEvent(event: HangarEvent) {
        when (event) {
            HangarEvent.AddAircraft -> navigator.navigate(NAV.HANGAR_DISCOVER.path)
            HangarEvent.OnDismissDialog -> {
                viewModelScope.launch {
                    supabase.realtime.removeChannel(channel!!)
                }
                _state.update { it.copy(selectedAircraft = null, droneStatus = null) }
            }

            is HangarEvent.OnSelectAircraft -> viewModelScope.launch { selectAircraft(event.aircraft) }
            HangarEvent.OnDeleteAircraft -> {
                viewModelScope.launch {
                    state.value.selectedAircraft?.let { hangarRepo.deleteAircraft(it.token) }
                    _state.update {
                        it.copy(
                            selectedAircraft = null
                        )
                    }
                }

            }
        }
    }


    private suspend fun selectAircraft(aircraft: Aircraft) {
        _state.update { it.copy(selectedAircraft = aircraft) }
        println(aircraft.token)
        channel = supabase.channel(aircraft.token.toString())
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