package planning.presentation.mission_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.domain.FlightDate
import repository.domain.InsertableMission
import planning.presentation.mission_editor.MissionEditorEvent.*
import repository.FlightDateRepo
import repository.MissionRepo

class MissionEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val missionRepo: MissionRepo by inject<MissionRepo>()
    private val flightDateRepo: FlightDateRepo by inject<FlightDateRepo>()

    init {
        loadDates()
    }

    private fun loadDates() {
        viewModelScope.launch {
            _state.update {
                val dates =
                    missionRepo.selectedMission.value?.id?.let { it1 -> flightDateRepo.getDates(it1) }
                        ?: listOf<FlightDate>().also {
                            navigator.navigate(
                                NAV.PLANNING.path
                            )
                        }
                it.copy(dates = dates)
            }
        }
    }

    private val _state = run {
        val selectedMission = missionRepo.selectedMission.value
        val editedMission = selectedMission?.let { InsertableMission(it.description, it.id) }
            ?: InsertableMission("")
        MutableStateFlow(
            MissionEditorState(
                selectedMission, editedMission, emptyList()
            )
        )
    }
    val state = _state.asStateFlow()

    fun onEvent(event: MissionEditorEvent) {
        when (event) {
            is UpdateMission -> {
                _state.value = _state.value.copy(editedMission = event.mission)
            }

            SaveMission -> viewModelScope.launch {
                val selectedMission = missionRepo.selectedMission.value
                if (selectedMission == null || selectedMission.description != _state.value.editedMission.description) {
                    missionRepo.selectedMission.value =
                        missionRepo.upsertMission(_state.value.editedMission)
                }
            }

            Cancel -> {
                missionRepo.selectedMission.value = null
                navigator.navigate(NAV.PLANNING.path)
            }

            ResetMission -> _state.update {
                it.copy(editedMission = _state.value.selectedMission?.let { mission ->
                    InsertableMission(
                        mission.description
                    )
                } ?: InsertableMission(""))
            }

            EditFlightPlan -> navigator.navigate(NAV.FLIGHT_PLAN_EDITOR.path)
            AddFlightDate -> navigator.navigate(NAV.FLIGHT_DATE_EDITOR.path)
            is DateSelected -> {
                flightDateRepo.selectedFlightDate.update {
                    event.date
                }
                navigator.navigate(NAV.FLIGHT_DATE_EDITOR.path)
            }
        }
    }
}