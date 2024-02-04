package planning.presentation.mission_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.UpdaterResult
import repository.domain.FlightDate
import repository.domain.InsertableMission
import planning.presentation.mission_editor.MissionEditorEvent.*
import repository.FlightDateRepo
import repository.MissionRepo
import repository.domain.Mission

class MissionEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val missionRepo: MissionRepo by inject<MissionRepo>()
    private val flightDateRepo: FlightDateRepo by inject<FlightDateRepo>()

    init {
        loadDates()
    }

    private fun loadDates() {
        viewModelScope.launch {
            val selectedMissionId = missionRepo.selectedMission.value?.id
            selectedMissionId?.let { missionId ->
                flightDateRepo.getDates(missionId).collect { response ->
                    when (response) {
                        is StoreReadResponse.Data -> _state.update {
                            it.copy(
                                dates = response.value,
                                datesLoading = false
                            )
                        }

                        is StoreReadResponse.Error.Custom<*> -> TODO()
                        is StoreReadResponse.Error.Exception -> TODO()
                        is StoreReadResponse.Error.Message -> TODO()
                        StoreReadResponse.Initial -> TODO()
                        is StoreReadResponse.Loading -> _state.update { it.copy(datesLoading = true) }
                        is StoreReadResponse.NoNewData -> TODO()
                    }
                }
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
                    val result = missionRepo.upsertMission(_state.value.editedMission)
                    missionRepo.selectedMission.value = result
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