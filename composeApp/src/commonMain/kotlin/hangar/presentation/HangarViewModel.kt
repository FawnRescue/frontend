package hangar.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.AircraftStatus
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
import kotlinx.coroutines.runBlocking
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.AircraftRepo
import repository.domain.Aircraft
import repository.domain.InsertableAircraft
import repository.domain.UserId

class HangarViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private var channel: RealtimeChannel? = null
    private val aircraftRepo by inject<AircraftRepo>()


    private val _state = MutableStateFlow(HangarState(null, null, null, null))
    val state = _state.asStateFlow()

    override fun onCleared() {
        runBlocking {
            channel?.unsubscribe()
        }
        super.onCleared()
    }

    init {
        loadAircrafts()
    }

    private fun loadAircrafts() {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return
        val userId = UserId(authId)
        viewModelScope.launch {
            aircraftRepo.getAircrafts(userId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> _state.update {
                        it.copy(
                            aircrafts = response.value,
                            loading = false
                        )
                    }

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
                _state.update { it.copy(selectedAircraft = null, aircraftStatus = null) }
            }

            is HangarEvent.OnSelectAircraft -> viewModelScope.launch { selectAircraft(event.aircraft) }
            HangarEvent.OnDeleteAircraft -> {
                viewModelScope.launch {
                    state.value.selectedAircraft?.let { aircraftRepo.deleteAircraft(it.token) }
                    _state.update {
                        it.copy(
                            selectedAircraft = null
                        )
                    }
                }

            }

            is HangarEvent.OnFOVChange -> _state.update {
                it.copy(
                    editableAircraft = state.value.editableAircraft?.copy(cameraFOV = event.fov)
                )
            }

            HangarEvent.OnEditAircraft -> _state.update {
                it.copy(
                    editableAircraft = state.value.selectedAircraft
                )
            }

            HangarEvent.OnSaveAircraft -> {
                val aircraft = state.value.editableAircraft
                if (!state.value.editable || aircraft == null) return
                viewModelScope.launch {
                    with(aircraft) {
                        val updatedAircraft = aircraftRepo.upsertAircraft(
                            InsertableAircraft(
                                token.id,
                                owner.id,
                                name,
                                description,
                                created_at.toString(),
                                deleted,
                                cameraFOV,
                                flightHeight
                            )
                        )
                        selectAircraft(updatedAircraft)
                        loadAircrafts()
                    }
                }
            }

            is HangarEvent.OnFlightHeightChange -> _state.update {
                it.copy(
                    editableAircraft = state.value.editableAircraft?.copy(flightHeight = event.height)
                )
            }
        }
    }


    private suspend fun selectAircraft(aircraft: Aircraft) {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return
        val userId = UserId(authId)
        _state.update {
            it.copy(
                selectedAircraft = aircraft,
                editableAircraft = null,
                editable = aircraft.owner == userId
            )
        }
        println(aircraft.token)
        channel = supabase.channel(aircraft.token.toString())
        val broadcastFlow = channel!!.broadcastFlow<AircraftStatus>(event = "aircraft_status")
        viewModelScope.launch {
            broadcastFlow.collect { status ->
                _state.update { it.copy(aircraftStatus = status) }
            }
        }
        println("Subscribing")
        channel!!.subscribe(blockUntilSubscribed = true)
        println("Subscribed")
    }
}