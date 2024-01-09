package planning.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.presentation.domain.Mission

class MissionListViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val _state = MutableStateFlow(MissionListState(missions = emptyList()))
    val state = _state.asStateFlow()
     init {
         loadMissions()
     }
    private fun loadMissions() {
        viewModelScope.launch {
            _state.value = MissionListState(
                missions = supabase.postgrest.from("mission").select().also { println("LOADING MISSIONS") }.decodeList<Mission>()
            )

        }
    }

    fun onEvent(event: MissionListEvent) {
        when (event) {
            MissionListEvent.CreateNewMission -> TODO()
            is MissionListEvent.ExistingMissionSelected -> TODO()
        }
    }
}