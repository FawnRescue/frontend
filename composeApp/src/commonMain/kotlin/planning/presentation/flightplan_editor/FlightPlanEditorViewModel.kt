package planning.presentation.flightplan_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import presentation.maps.getCenter
import repository.FlightPlanRepo
import repository.MissionRepo
import repository.domain.InsertableFlightPlan
import repository.domain.insertable
import repository.sortPolarCoordinates

class FlightPlanEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val missionRepo by inject<MissionRepo>()
    private val flightPlanRepo by inject<FlightPlanRepo>()
    private val _state = MutableStateFlow(
        FlightPlanEditorState(
            selectedMission = missionRepo.selectedMission.value,
        )
    )

    init {
        val selectedMission = missionRepo.selectedMission.value
        if (selectedMission?.plan != null) {
            viewModelScope.launch {
                flightPlanRepo.getPlan(selectedMission.plan).collect { response ->
                    when (response) {
                        is StoreReadResponse.Data -> _state.update {
                            it.copy(
                                selectedFlightPlan = response.value.first(),
                                editedBoundary = response.value.first().boundary,
                                editedCheckpoints = response.value.first().checkpoints,
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


    fun onEvent(event: FlightPlanEditorEvent) {
        when (event) {
            is FlightPlanEditorEvent.MarkerAdded -> _state.update {
                val newBoundary = _state.value.editedBoundary.plus(
                    event.location
                ).sortPolarCoordinates()
                it.copy(
                    editedBoundary = newBoundary,
                    editedCheckpoints = flightPlanRepo.calculateCheckpoints(
                        newBoundary
                    )
                )
            }

            is FlightPlanEditorEvent.MarkerRemoved -> _state.update {
                val newBoundary = _state.value.editedBoundary.minus(
                    event.location
                ).sortPolarCoordinates()
                it.copy(
                    editedBoundary = newBoundary,
                    editedCheckpoints = flightPlanRepo.calculateCheckpoints(newBoundary)
                )
            }

            FlightPlanEditorEvent.SaveBoundary -> {
                val selectedMission = missionRepo.selectedMission.value
                if (selectedMission == null) {
                    navigator.goBack()
                    return
                }
                viewModelScope.launch {
                    flightPlanRepo.upsertFlightPlan(
                        selectedMission.insertable(), InsertableFlightPlan(
                            id = _state.value.selectedFlightPlan?.id,
                            boundary = _state.value.editedBoundary,
                            location = _state.value.editedBoundary.getCenter(),
                            checkpoints = flightPlanRepo.calculateCheckpoints(_state.value.editedBoundary)
                        )
                    ).also {
                        navigator.navigate(NAV.MISSION_EDITOR.path)
                    }
                }
            }

            FlightPlanEditorEvent.ToggleBoundary -> _state.update { it.copy(showBoundary = !it.showBoundary) }
            FlightPlanEditorEvent.ToggleBoundaryMarkers -> _state.update {
                it.copy(
                    showBoundaryMarkers = !it.showBoundaryMarkers
                )
            }

            FlightPlanEditorEvent.ToggleCheckpointMarkers -> _state.update {
                it.copy(
                    showCheckpointMarkers = !it.showCheckpointMarkers
                )
            }

            FlightPlanEditorEvent.TogglePath -> _state.update { it.copy(showPath = !it.showPath) }
            FlightPlanEditorEvent.ToggleLayers -> _state.update { it.copy(showLayers = !it.showLayers) }
        }
    }
}