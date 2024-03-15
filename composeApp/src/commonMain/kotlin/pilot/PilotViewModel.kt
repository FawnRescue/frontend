package pilot

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import hangar.domain.AircraftStatus
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.realtime.PostgresAction.Insert
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcast
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse.Data
import org.mobilenativefoundation.store.store5.StoreReadResponse.Error
import org.mobilenativefoundation.store.store5.StoreReadResponse.Initial
import org.mobilenativefoundation.store.store5.StoreReadResponse.Loading
import org.mobilenativefoundation.store.store5.StoreReadResponse.NoNewData
import pilot.PilotEvent.NoPlan
import pilot.RescuerRole.PILOT
import pilot.RescuerRole.RESCUER
import presentation.maps.LatLong
import repository.AircraftRepo
import repository.CommandRepo
import repository.DetectionRepo
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.ImageDataKey
import repository.ImageDataRepo
import repository.ImageRepo
import repository.LocationService
import repository.MissionRepo
import repository.domain.AircraftId
import repository.domain.FlightDateId
import repository.domain.FlightPlanId
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
    private val imageDataRepo by inject<ImageDataRepo>()
    private val imageRepo by inject<ImageRepo>()
    private val detectionRepo by inject<DetectionRepo>()
    private val locationService by inject<LocationService>()
    private val _state = MutableStateFlow(PilotState(null, null, null, null, null))
    val state = _state.asStateFlow()
    private var globalChannel: RealtimeChannel? = null
    override fun onCleared() {
        runBlocking {
            globalChannel?.unsubscribe()
        }
        super.onCleared()
    }

    init {
        val date = flightDateRepo.selectedFlightDate.value
        if (date == null) {
            Napier.e("No Date selected")
        } else {
            _state.update { it.copy(date = flightDateRepo.selectedFlightDate.value) }
            val channel = supabase.channel(date.aircraft)
            globalChannel = channel
            viewModelScope.launch {
                channel.subscribe(blockUntilSubscribed = true)
            }
            // TODO Parse date to domain ids instead of casting here
            val flightDateId = FlightDateId(date.id)

            val authId = supabase.auth.currentUserOrNull()?.id
            if (authId != null) {
                val userId = UserId(authId)
                loadAircraft(AircraftId(date.aircraft), userId)
                publishOwnLocation(channel, flightDateId, userId)
            }

            loadMission(MissionId(date.mission))
            loadDetections(flightDateId)
            collectDetectionLocations(channel)
            collectAircraftStatus(channel)
            collectHelperLocations(channel)
        }
    }

    private fun loadDetections(flightDateId: FlightDateId) {
        viewModelScope.launch {
            detectionRepo.getDetections(flightDateId).collect { response ->
                when (response) {
                    is Data -> {
                        if (response.value.isEmpty()) {
                            return@collect
                        }
                        _state.update { s ->
                            s.copy(
                                detections = _state.value.detections.plus(
                                    response.value
                                )
                            )
                        }
                    }

                    is Error.Custom<*> -> TODO()
                    is Error.Exception -> TODO()
                    is Error.Message -> TODO()
                    Initial -> TODO()
                    is Loading -> {}
                    is NoNewData -> {}
                }
            }
        }
    }

    private fun collectDetectionLocations(channel: RealtimeChannel) {
        val changeFlow =
            channel.postgresChangeFlow<Insert>(schema = "public") {
                table = "detection"
                //filter("flight_date", FilterOperator.EQ, flightDateId.id)
            }
        viewModelScope.launch {
            changeFlow.collect { newRow ->
                val detection = newRow.decodeRecord<NetworkDetection>().toLocal()
                _state.update { s ->
                    s.copy(
                        detections = _state.value.detections.plus(
                            detection
                        )
                    )
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
        userId: UserId,
    ) {

        viewModelScope.launch {
            locationService.location().collect { ownLocation ->
                _state.update {
                    it.copy(
                        helperLocations = it.helperLocations.plus(
                            Pair(
                                userId,
                                PersonLocation(
                                    ownLocation,
                                    if (state.value.isPilot) PILOT else RESCUER
                                )
                            )
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

    private fun loadAircraft(aircraftId: AircraftId, userId: UserId) {
        viewModelScope.launch {
            aircraftRepo.getAircraft(aircraftId)
                .collect { response ->
                    when (response) {
                        is Data -> {
                            if (response.value.isEmpty()) {
                                _state.update { it.copy(loading = false) }
                                Napier.e("No Aircraft loaded")
                                return@collect
                            }
                            val aircraft = response.value[0]
                            _state.update {
                                it.copy(
                                    aircraft = aircraft,
                                    loading = false,
                                    isPilot = aircraft.owner == userId
                                )
                            }
                        }

                        is Error.Custom<*> -> TODO()
                        is Error.Exception -> {
                            response.error.message?.let { Napier.e(it) }
                        }

                        is Error.Message -> TODO()
                        Initial -> TODO()
                        is Loading -> _state.update { it.copy(loading = true) }
                        is NoNewData -> _state.update { it.copy(loading = false) }
                    }
                }
        }
    }

    private fun collectAircraftStatus(channel: RealtimeChannel) {
        val broadcastFlow =
            channel.broadcastFlow<AircraftStatus>(event = "aircraft_status")
        viewModelScope.launch {
            broadcastFlow.collect { status ->
                _state.update { it.copy(aircraftStatus = status) }
            }
        }
    }

    private fun loadMission(missionId: MissionId) {
        viewModelScope.launch {
            missionRepo.getMission(missionId).collect { response ->
                when (response) {
                    is Data -> {
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

                    is Error.Custom<*> -> TODO()
                    is Error.Exception -> TODO()
                    is Error.Message -> TODO()
                    Initial -> TODO()
                    is Loading -> _state.update { it.copy(loading = true) }
                    is NoNewData -> _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    private fun loadPlan(planId: FlightPlanId) {
        viewModelScope.launch {
            flightPlanRepo.getPlan(planId).collect { response ->
                when (response) {
                    is Data -> _state.update {
                        it.copy(
                            plan = response.value.firstOrNull(),
                            planLoading = false
                        )
                    }

                    is Error.Custom<*> -> TODO()
                    is Error.Exception -> TODO()
                    is Error.Message -> TODO()
                    Initial -> TODO()
                    is Loading -> _state.update { it.copy(planLoading = true) }
                    is NoNewData -> TODO()
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

            is PilotEvent.DetectionSelected -> {
                // load the corresponding image
                _state.update {
                    it.copy(
                        selectedDetection = event.detection
                    )
                }
                viewModelScope.launch {
                    imageRepo.getImage(event.detection.image).collect { imageResponse ->
                        when (imageResponse) {
                            is Data -> {
                                if (imageResponse.value.isEmpty()) {
                                    Napier.e { "No DB Image entry found for detection" }
                                    return@collect
                                }
                                val image = imageResponse.value.first()
                                if (image.thermal_path == null && image.rgb_path == null) {
                                    Napier.e { "No image found for detection" }
                                    return@collect
                                }

                                image.thermal_path?.let {
                                    viewModelScope.launch {
                                        imageDataRepo.store.stream(
                                            StoreReadRequest.cached(
                                                ImageDataKey.Read.ByFileName(
                                                    image.thermal_path
                                                ), false
                                            )
                                        ).collect { imageDataResponse ->
                                            when (imageDataResponse) {
                                                is Data -> {
                                                    _state.update {
                                                        it.copy(selectedDetectionThermalImageData = imageDataResponse.value.firstOrNull())
                                                    }
                                                }

                                                is Error.Custom<*> -> TODO()
                                                is Error.Exception -> {
                                                    Napier.e(
                                                        "Image Data loading failed",
                                                        imageDataResponse.error
                                                    )
                                                }

                                                is Error.Message -> {
                                                    Napier.e(
                                                        imageDataResponse.message
                                                    )
                                                }

                                                Initial -> TODO()
                                                is Loading -> {

                                                }

                                                is NoNewData -> TODO()
                                            }

                                        }
                                    }
                                }
                                image.rgb_path?.let {
                                    viewModelScope.launch {
                                        imageDataRepo.store.stream(
                                            StoreReadRequest.cached(
                                                ImageDataKey.Read.ByFileName(
                                                    image.rgb_path
                                                ), false
                                            )
                                        ).collect { imageDataResponse ->
                                            when (imageDataResponse) {
                                                is Data -> {
                                                    _state.update {
                                                        it.copy(selectedDetectionRGBImageData = imageDataResponse.value.firstOrNull())
                                                    }
                                                }

                                                is Error.Custom<*> -> TODO()
                                                is Error.Exception -> {
                                                    Napier.e(
                                                        "Image Data loading failed",
                                                        imageDataResponse.error
                                                    )
                                                }

                                                is Error.Message -> {
                                                    Napier.e(
                                                        imageDataResponse.message
                                                    )
                                                }

                                                Initial -> TODO()
                                                is Loading -> {

                                                }

                                                is NoNewData -> TODO()
                                            }

                                        }
                                    }
                                }

                            }

                            is Error.Custom<*> -> TODO()
                            is Error.Exception -> {
                                Napier.e(
                                    "Image loading failed",
                                    imageResponse.error
                                )
                            }

                            is Error.Message -> {
                                Napier.e(
                                    imageResponse.message
                                )
                            }

                            Initial -> TODO()
                            is Loading -> {

                            }

                            is NoNewData -> TODO()
                        }

                    }
                }
            }

            PilotEvent.DetectionDeselected -> {
                _state.update {
                    it.copy(
                        selectedDetection = null,
                        selectedDetectionRGBImageData = null,
                        selectedDetectionThermalImageData = null
                    )
                }
            }
        }
    }
}