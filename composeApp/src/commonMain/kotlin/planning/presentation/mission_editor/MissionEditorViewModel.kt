package planning.presentation.mission_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import planning.presentation.mission_editor.MissionEditorEvent.AddFlightDate
import planning.presentation.mission_editor.MissionEditorEvent.Cancel
import planning.presentation.mission_editor.MissionEditorEvent.DateSelected
import planning.presentation.mission_editor.MissionEditorEvent.EditFlightPlan
import planning.presentation.mission_editor.MissionEditorEvent.EditMission
import planning.presentation.mission_editor.MissionEditorEvent.SaveMission
import planning.presentation.mission_editor.MissionEditorEvent.UpdateMission
import repository.FlightDateRepo
import repository.MissionRepo
import repository.domain.InsertableMission
import repository.domain.UserId

class MissionEditorViewModel : ViewModel(), KoinComponent {
    private val navigator by inject<Navigator>()
    private val missionRepo by inject<MissionRepo>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val supabase by inject<SupabaseClient>()

    private val _state = run {
        val selectedMission = missionRepo.selectedMission.value
        MutableStateFlow(
            MissionEditorState(
                selectedMission
            )
        )
    }

    init {
        supabase.auth.currentUserOrNull()?.id?.let { authId ->
            _state.update {
                val selectedMission = missionRepo.selectedMission.value
                it.copy(editable = selectedMission == null || selectedMission.owner == UserId(authId))
            }
        }
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

    val state = _state.asStateFlow()

    fun onEvent(event: MissionEditorEvent) {
        when (event) {
            is UpdateMission -> {
                _state.value = _state.value.copy(editedMission = event.mission)
            }

            SaveMission -> viewModelScope.launch {
                val selectedMission = missionRepo.selectedMission.value
                val editedMission = _state.value.editedMission ?: return@launch
                if (selectedMission == null || (selectedMission.description != editedMission.description)) {
                    val newMission = missionRepo.upsertMission(editedMission)
                    missionRepo.selectedMission.value = newMission
                    _state.update {
                        it.copy(
                            selectedMission = newMission
                        )
                    }
                }
                _state.update { it.copy(editedMission = null) }
            }

            Cancel -> {
                missionRepo.selectedMission.value = null
                navigator.navigate(NAV.PLANNING.path)
            }

            EditMission -> _state.update {
                it.copy(editedMission = _state.value.selectedMission?.let { mission ->
                    InsertableMission(
                        mission.description
                    )
                } ?: InsertableMission(""))
            }

            EditFlightPlan -> navigator.navigate(NAV.FLIGHT_PLAN_EDITOR.path)
            AddFlightDate -> {
                flightDateRepo.selectedFlightDate.update { null }
                navigator.navigate(NAV.FLIGHT_DATE_EDITOR.path)
            }

            is DateSelected -> {
                flightDateRepo.selectedFlightDate.update {
                    event.date
                }
                navigator.navigate(NAV.FLIGHT_DATE_EDITOR.path)
            }
        }
    }
}