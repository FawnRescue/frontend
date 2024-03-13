package pilot

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.AircraftStatus
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcast
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.supabaseJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import pilot.PilotEvent.NoPlan
import pilot.RescuerRole.*
import presentation.maps.LatLong
import repository.AircraftRepo
import repository.CommandRepo
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.ImageRepo
import repository.LocationRepo
import repository.LocationService
import repository.MissionRepo
import repository.domain.Aircraft
import repository.domain.AircraftId
import repository.domain.Detection
import repository.domain.FlightDateId
import repository.domain.FlightPlanId
import repository.domain.ImageId
import repository.domain.MissionId
import repository.domain.NetworkDetection
import repository.domain.UserId
import repository.domain.toLocal

@Serializable
data class LocationUpdate(
    val userId: UserId,
    val flightDateId: FlightDateId,
    val location: LatLong,
    val role: RescuerRole,
)

class PilotViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val flightPlanRepo by inject<FlightPlanRepo>()
    private val missionRepo by inject<MissionRepo>()
    private val aircraftRepo by inject<AircraftRepo>()
    private val commandRepo by inject<CommandRepo>()
    private val imageRepo by inject<ImageRepo>()
    private val locationService by inject<LocationService>()
    private val _state = MutableStateFlow(PilotState(null, null, null, null, null))
    val state = _state.asStateFlow()

    init {
        val date = flightDateRepo.selectedFlightDate.value
        if (date == null) {
            Napier.e("No Date selected")
        } else {
            _state.update { it.copy(date = flightDateRepo.selectedFlightDate.value) }
            // TODO Parse date to domain ids instead of casting here
            loadAircraft(AircraftId(date.aircraft), FlightDateId(date.id))
            loadMission(MissionId(date.mission))
            collectDetectionLocations(FlightDateId(date.id))
        }
    }

    private fun collectDetectionLocations(flightDateId: FlightDateId) {
        val detectionChannel = supabase.channel("public:detection")
        val changeFlow =
            detectionChannel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                table = "detection"
                filter("flight_date", FilterOperator.EQ, flightDateId)
            }
        viewModelScope.launch {
            changeFlow.collect { newRow ->
                val detection = newRow.decodeRecord<NetworkDetection>().toLocal()
                imageRepo.getImage(detection.image).collect { response ->
                    when (response) {
                        is StoreReadResponse.Data -> _state.update { s ->
                            s.copy(
                                detections = _state.value.detections.plus(
                                    response.value.map { DetectionLocation(it.location) })
                            )
                        }

                        is StoreReadResponse.Error.Exception -> Napier.e(
                            "Image loading error",
                            response.error
                        )

                        is StoreReadResponse.Error.Message -> Napier.e(response.message)
                        is StoreReadResponse.Loading -> {}
                        is StoreReadResponse.NoNewData -> {}
                        is StoreReadResponse.Error.Custom<*> -> TODO()
                        StoreReadResponse.Initial -> TODO()
                    }
                }
            }
        }
    }

    private fun collectHelperLocations(channel: RealtimeChannel) {
        val broadcastFlow =
            channel.broadcastFlow<LocationUpdate>(event = "location")
        viewModelScope.launch {
            broadcastFlow.collect { update ->
                if (update.flightDateId.id != state.value.date?.id) {
                    return@collect
                }
                _state.update {
                    it.copy(
                        helperLocations = it.helperLocations.plus(
                            Pair(
                                update.userId,
                                PersonLocation(update.location, update.role)
                            )
                        )
                    )
                }
            }
        }
    }

    private fun publishOwnLocation(
        channel: RealtimeChannel,
        flightDateId: FlightDateId,
    ) {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return
        val userId = UserId(authId)
        viewModelScope.launch {
            locationService.location().collect { ownLocation ->
                _state.update {
                    it.copy(
                        ownLocation = PersonLocation(
                            ownLocation,
                            if (state.value.aircraft?.owner == userId) PILOT else RESCUER
                        )
                    )
                }
                val update = LocationUpdate(
                    userId,
                    flightDateId,
                    ownLocation,
                    if (state.value.aircraft?.owner == userId) PILOT else RESCUER
                )
                channel.broadcast("location", update)
            }
        }
    }

    private fun loadAircraft(aircraftId: AircraftId, flightDateId: FlightDateId) {
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
                            val channel = supabase.channel(aircraft.token.toString())
                            collectAircraftStatus(channel)
                            publishOwnLocation(channel, flightDateId)
                            collectHelperLocations(channel)
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

    private suspend fun collectAircraftStatus(channel: RealtimeChannel) {
        val broadcastFlow =
            channel.broadcastFlow<AircraftStatus>(event = "aircraft_status")
        viewModelScope.launch {
            broadcastFlow.collect { status ->
                _state.update { it.copy(aircraftStatus = status) }
            }
        }
        println("Subscribing")
        channel.subscribe(blockUntilSubscribed = true)
        println("Subscribed")
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
            flightPlanRepo.getPlan(planId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> _state.update {
                        it.copy(
                            plan = response.value.firstOrNull(),
                            planLoading = false
                        )
                    }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    is StoreReadResponse.Error.Exception -> TODO()
                    is StoreReadResponse.Error.Message -> TODO()
                    StoreReadResponse.Initial -> TODO()
                    is StoreReadResponse.Loading -> _state.update { it.copy(planLoading = true) }
                    is StoreReadResponse.NoNewData -> TODO()
                }
            }
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