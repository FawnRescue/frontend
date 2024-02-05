package pilot

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
import pilot.PilotEvent.NoPlan
import repository.FlightDateRepo
import repository.FlightPlanRepo
import repository.MissionRepo

class PilotViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val flightPlanRepo by inject<FlightPlanRepo>()
    private val missionRepo by inject<MissionRepo>()
    private val _state = MutableStateFlow(PilotState(null, null, null))
    val state = _state.asStateFlow()

    init {
        val date = flightDateRepo.selectedFlightDate.value
        if (date == null) {
            navigator.navigate(NAV.HOME.path)

        } else {
            _state.update { it.copy(date = flightDateRepo.selectedFlightDate.value) }
            viewModelScope.launch {
                val mission = missionRepo.getMission(date.mission)
                _state.update { it.copy(mission = mission) }
                if (mission.plan == null) {
                    navigator.navigate(NAV.HOME.path)
                } else {
                    val plan = flightPlanRepo.getPlan(mission.plan)
                    _state.update { it.copy(plan = plan) }
                }

            }
        }
    }

    fun onEvent(event: PilotEvent) {
        when (event) {
            NoPlan -> {
                navigator.navigate(NAV.HOME.path)
            }

        }
    }
}