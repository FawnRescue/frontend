package pilot

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.AircraftStatus
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import pilot.PilotEvent.NoPlan
import repository.AircraftRepo
import repository.CommandRepo
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.MissionRepo
import repository.domain.AircraftId
import repository.domain.FlightPlanId
import repository.domain.MissionId

class PilotViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val flightPlanRepo by inject<FlightPlanRepo>()
    private val missionRepo by inject<MissionRepo>()
    private val aircraftRepo by inject<AircraftRepo>()
    private val commandRepo by inject<CommandRepo>()
    private val _state = MutableStateFlow(PilotState(null, null, null, null, null))
    val state = _state.asStateFlow()
    private var channel: RealtimeChannel? = null

    init {
        val date = flightDateRepo.selectedFlightDate.value
        if (date == null) {
            Napier.e("No Date selected")
        } else {
            _state.update { it.copy(date = flightDateRepo.selectedFlightDate.value) }
            // TODO Parse date to domain ids instead of casting here
            loadMission(MissionId(date.mission))
            loadAircraft(AircraftId(date.aircraft))
        }
    }

    private fun loadAircraft(aircraftId: AircraftId) {
        viewModelScope.launch {
            aircraftRepo.getAircraft(aircraftId)
                .collect { response ->
                    when (response) {
                        is StoreReadResponse.Data -> {
                            if (response.value.isEmpty()) {
                                _state.update { it.copy(loading = false) }
                                Napier.e("No Aircraft loaded")
                                return@collect
                            }
                            val aircraft = response.value[0]
                            _state.update { it.copy(aircraft = aircraft, loading = false) }
                            channel = supabase.channel(aircraft.token.toString())
                            val broadcastFlow =
                                channel!!.broadcastFlow<AircraftStatus>(event = "event")
                            viewModelScope.launch {
                                broadcastFlow.collect { status ->
                                    _state.update { it.copy(aircraftStatus = status) }
                                }
                            }
                            println("Subscribing")
                            channel!!.subscribe(blockUntilSubscribed = true)
                            println("Subscribed")
                        }

                        is StoreReadResponse.Error.Custom<*> -> TODO()
                        is StoreReadResponse.Error.Exception -> {
                            response.error.message?.let { Napier.e(it) }
                        }

                        is StoreReadResponse.Error.Message -> TODO()
                        StoreReadResponse.Initial -> TODO()
                        is StoreReadResponse.Loading -> _state.update { it.copy(loading = true) }
                        is StoreReadResponse.NoNewData -> _state.update { it.copy(loading = false) }
                    }
                }
        }
    }

    private fun loadMission(missionId: MissionId) {
        viewModelScope.launch {
            missionRepo.getMission(missionId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> {
                        if (response.value.isEmpty()) {
                            _state.update { it.copy(loading = false) }
                            Napier.e("No Mission loaded")
                            return@collect
                        }
                        val mission = response.value[0]
                        _state.update { it.copy(mission = mission, loading = false) }
                        if (mission.plan == null) {
                            Napier.e("Mission loaded has no flight plan")
                        } else {
                            loadPlan(mission.plan)
                        }
                    }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    is StoreReadResponse.Error.Exception -> TODO()
                    is StoreReadResponse.Error.Message -> TODO()
                    StoreReadResponse.Initial -> TODO()
                    is StoreReadResponse.Loading -> _state.update { it.copy(loading = true) }
                    is StoreReadResponse.NoNewData -> _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    private fun loadPlan(planId: FlightPlanId) {
        viewModelScope.launch {
            _state.update { it.copy(plan = flightPlanRepo.getPlan(planId)) }
        }
    }

    fun onEvent(event: PilotEvent) {
        when (event) {
            NoPlan -> {
                navigator.navigate(NAV.HOME.path)
            }

            is PilotEvent.SendCommand -> {
                viewModelScope.launch {
                    commandRepo.sendCommand(event.command)
                }
            }
        }
    }
}