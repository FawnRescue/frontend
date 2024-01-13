package planning.presentation.flightplan_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NavigationEnum
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableFlightPlan
import planning.domain.insertable
import planning.repository.FlightPlanRepo
import planning.repository.MissionRepo
import presentation.maps.getCenter

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
                val plan = flightPlanRepo.getPath(selectedMission.plan)
                _state.update {
                    it.copy(selectedFlightPlan = plan, editedBoundary = plan.boundary)
                }
            }
        }
    }

    val state = _state.asStateFlow()


    fun onEvent(event: FlightPlanEditorEvent) {
        when (event) {
            is FlightPlanEditorEvent.MarkerAdded -> _state.update {
                it.copy(
                    editedBoundary = _state.value.editedBoundary.plus(event.location)
                )
            }

            FlightPlanEditorEvent.SaveBoundary -> {
                val selectedMission = missionRepo.selectedMission.value
                if (selectedMission == null) {
                    navigator.navigate(NavigationEnum.HOME.path)
                    return
                }
                viewModelScope.launch {
                    flightPlanRepo.upsertFlightPlan(
                        selectedMission.insertable(), InsertableFlightPlan(
                            boundary = _state.value.editedBoundary,
                            location = _state.value.editedBoundary.getCenter()
                        )
                    ).also {
                        navigator.navigate(NavigationEnum.PLANNING.path)
                    }

                }
            }
        }
    }
}