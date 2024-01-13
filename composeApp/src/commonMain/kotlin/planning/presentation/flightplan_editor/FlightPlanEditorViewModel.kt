package planning.presentation.flightplan_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.repository.MissionRepo

class FlightPlanEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    val missionRepo by inject<MissionRepo>()
    private val _state = MutableStateFlow(FlightPlanEditorState(missionRepo.selectedMission.value))
    val state = _state.asStateFlow()


    fun onEvent(event: FlightPlanEditorEvent) {
        when (event) {

            else -> {}
        }
    }
}