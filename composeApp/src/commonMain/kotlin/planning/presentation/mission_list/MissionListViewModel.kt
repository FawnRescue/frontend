package planning.presentation.mission_list

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.Mission
import planning.repository.MissionRepo

class MissionListViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val missionRepo: MissionRepo by inject<MissionRepo>()
    private val _state = MutableStateFlow(MissionListState(missions = emptyList()))
    val state = _state.asStateFlow()

    init {
        loadMissions()
    }

    private fun loadMissions() {
        viewModelScope.launch {
            _state.value = MissionListState(
                missions = missionRepo.getMissions()
            )

        }
    }

    private fun createMission() {
        viewModelScope.launch {
            navigator.navigate(NAV.MISSION_EDITOR.path)
        }
    }

    private fun editMission(mission: Mission) {
        missionRepo.selectedMission.value = mission
        viewModelScope.launch {
            navigator.navigate(NAV.MISSION_EDITOR.path)
        }
    }

    fun onEvent(event: MissionListEvent) {
        when (event) {
            MissionListEvent.CreateNewMission -> createMission()
            is MissionListEvent.ExistingMissionSelected -> editMission(event.mission)
        }
    }
}