package planning.presentation.flightdate_viewer

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import pilot.PilotEvent
import presentation.maps.getCenter
import repository.DetectionRepo
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.ImageDataKey
import repository.ImageDataRepo
import repository.ImageRepo
import repository.MissionRepo
import repository.domain.FlightDateId
import repository.domain.InsertableFlightPlan
import repository.domain.insertable
import repository.sortPolarCoordinates

class FlightDateViewerViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val missionRepo by inject<MissionRepo>()
    private val flightPlanRepo by inject<FlightPlanRepo>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val imageRepo by inject<ImageRepo>()
    private val imageDataRepo by inject<ImageDataRepo>()
    private val detectionRepo by inject<DetectionRepo>()
    private val _state = MutableStateFlow(
        FlightDateViewerState(
            selectedMission = missionRepo.selectedMission.value,
        )
    )

    init {
        val selectedMission = missionRepo.selectedMission.value
        val selectedFlightDate = flightDateRepo.selectedFlightDate.value
        if (selectedMission?.plan != null && selectedFlightDate != null) {
            loadDetections(FlightDateId(selectedFlightDate.id))
            viewModelScope.launch {
                flightPlanRepo.getPlan(selectedMission.plan).collect { response ->
                    when (response) {
                        is StoreReadResponse.Data -> _state.update {
                            it.copy(
                                selectedFlightPlan = response.value.first(),
                                boundary = response.value.first().boundary,
                                checkpoints = response.value.first().checkpoints,
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
    }

    val state = _state.asStateFlow()

    private fun loadDetections(flightDateId: FlightDateId) {
        viewModelScope.launch {
            detectionRepo.getDetections(flightDateId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> {
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

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    is StoreReadResponse.Error.Exception -> TODO()
                    is StoreReadResponse.Error.Message -> TODO()
                    StoreReadResponse.Initial -> TODO()
                    is StoreReadResponse.Loading -> {}
                    is StoreReadResponse.NoNewData -> {}
                }
            }
        }
    }


    fun onEvent(event: FlightDateViewerEvent) {
        when (event) {
            FlightDateViewerEvent.ToggleBoundary -> _state.update { it.copy(showBoundary = !it.showBoundary) }
            FlightDateViewerEvent.ToggleBoundaryMarkers -> _state.update {
                it.copy(
                    showBoundaryMarkers = !it.showBoundaryMarkers
                )
            }

            FlightDateViewerEvent.ToggleCheckpointMarkers -> _state.update {
                it.copy(
                    showCheckpointMarkers = !it.showCheckpointMarkers
                )
            }

            FlightDateViewerEvent.TogglePath -> _state.update { it.copy(showPath = !it.showPath) }
            FlightDateViewerEvent.Cancel -> {
                navigator.goBack()
            }

            FlightDateViewerEvent.ToggleDetections -> _state.update {
                it.copy(
                    showDetectionMarkers = !it.showDetectionMarkers
                )
            }

            is FlightDateViewerEvent.DetectionSelected -> {
                // load the corresponding image
                _state.update {
                    it.copy(
                        selectedDetection = event.detection
                    )
                }
                viewModelScope.launch {
                    imageRepo.getImage(event.detection.image).collect { imageResponse ->
                        when (imageResponse) {
                            is StoreReadResponse.Data -> {
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
                                                is StoreReadResponse.Data -> {
                                                    _state.update {
                                                        it.copy(selectedDetectionThermalImageData = imageDataResponse.value.firstOrNull())
                                                    }
                                                }

                                                is StoreReadResponse.Error.Custom<*> -> TODO()
                                                is StoreReadResponse.Error.Exception -> {
                                                    Napier.e(
                                                        "Image Data loading failed",
                                                        imageDataResponse.error
                                                    )
                                                }

                                                is StoreReadResponse.Error.Message -> {
                                                    Napier.e(
                                                        imageDataResponse.message
                                                    )
                                                }

                                                StoreReadResponse.Initial -> TODO()
                                                is StoreReadResponse.Loading -> {

                                                }

                                                is StoreReadResponse.NoNewData -> TODO()
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
                                                is StoreReadResponse.Data -> {
                                                    _state.update {
                                                        it.copy(selectedDetectionRGBImageData = imageDataResponse.value.firstOrNull())
                                                    }
                                                }

                                                is StoreReadResponse.Error.Custom<*> -> TODO()
                                                is StoreReadResponse.Error.Exception -> {
                                                    Napier.e(
                                                        "Image Data loading failed",
                                                        imageDataResponse.error
                                                    )
                                                }

                                                is StoreReadResponse.Error.Message -> {
                                                    Napier.e(
                                                        imageDataResponse.message
                                                    )
                                                }

                                                StoreReadResponse.Initial -> TODO()
                                                is StoreReadResponse.Loading -> {

                                                }

                                                is StoreReadResponse.NoNewData -> TODO()
                                            }

                                        }
                                    }
                                }

                            }

                            is StoreReadResponse.Error.Custom<*> -> TODO()
                            is StoreReadResponse.Error.Exception -> {
                                Napier.e(
                                    "Image loading failed",
                                    imageResponse.error
                                )
                            }

                            is StoreReadResponse.Error.Message -> {
                                Napier.e(
                                    imageResponse.message
                                )
                            }

                            StoreReadResponse.Initial -> TODO()
                            is StoreReadResponse.Loading -> {

                            }

                            is StoreReadResponse.NoNewData -> TODO()
                        }

                    }
                }
            }

            FlightDateViewerEvent.DetectionDeselected -> {
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