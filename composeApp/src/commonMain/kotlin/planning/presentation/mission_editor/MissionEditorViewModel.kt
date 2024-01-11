package planning.presentation.mission_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NavigationEnum
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.InsertableMission
import planning.repository.MissionRepo

class MissionEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val missionRepo: MissionRepo by inject<MissionRepo>()

    private val _state = run {
        val selectedMission = missionRepo.selectedMission.value
        val editedMission = selectedMission?.let { InsertableMission(it.description) }
            ?: InsertableMission("")
        MutableStateFlow(
            MissionEditorState(
                selectedMission,
                editedMission
            )
        )
    }
    val state = _state.asStateFlow()

    fun onEvent(event: MissionEditorEvent) {
        when (event) {
            is MissionEditorEvent.UpdateMission -> {
                _state.value = _state.value.copy(editedMission = event.mission)
            }

            MissionEditorEvent.SaveMission -> viewModelScope.launch {
                missionRepo.selectedMission.value =
                    missionRepo.createMission(_state.value.editedMission)
                //TODO redirect to pathplanner
            }

            MissionEditorEvent.Cancel -> {
                missionRepo.selectedMission.value = null
                navigator.navigate(NavigationEnum.HOME.path)
            }

            MissionEditorEvent.ResetMission -> _state.update {
                it.copy(editedMission = _state.value.selectedMission?.let { mission ->
                    InsertableMission(
                        mission.description
                    )
                } ?: InsertableMission(""))
            }
        }
    }
}