package planning.presentation.mission_list

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.aakira.napier.Napier
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
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import planning.presentation.mission_list.MissionListEvent.CreateNewMission
import planning.presentation.mission_list.MissionListEvent.ExistingMissionSelected
import repository.MissionKey
import repository.MissionRepo
import repository.domain.Mission
import repository.domain.UserId

class MissionListViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    private val missionRepo by inject<MissionRepo>()
    private val supabase by inject<SupabaseClient>()
    private val _state =
        MutableStateFlow(MissionListState(ownMissions = emptyList(), otherMissions = emptyList()))
    val state = _state.asStateFlow()

    init {
        missionRepo.selectedMission.value = null
        loadMissions()
    }


    private fun loadMissions() {
        val userId = supabase.auth.currentUserOrNull()?.id?.let { UserId(it) }
        viewModelScope.launch {
            missionRepo.store.stream(
                StoreReadRequest.cached(
                    MissionKey.Read.ByOwner, true
                )
            ).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> _state.update {
                        it.copy(
                            ownMissions = response.value.filter { mission -> mission.owner == userId },
                            otherMissions = response.value.filter { mission -> mission.owner != userId },
                            loading = false
                        )
                    }

                    is StoreReadResponse.Error.Exception -> Napier.e(
                        "Mission loading error",
                        response.error
                    )

                    is StoreReadResponse.Error.Message -> Napier.e(response.message)
                    is StoreReadResponse.Loading -> _state.update {
                        it.copy(loading = true)
                    }

                    is StoreReadResponse.NoNewData -> _state.update {
                        it.copy(loading = false)
                    }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    StoreReadResponse.Initial -> TODO()
                }
            }


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
            CreateNewMission -> createMission()
            is ExistingMissionSelected -> editMission(event.mission)
        }
    }
}