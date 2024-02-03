package pilot

import pilot.PilotEvent.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.repository.FlightPlanRepo

class PilotViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightPlanRepo by inject<FlightPlanRepo>()
    private val _state = MutableStateFlow(PilotState(flightPlanRepo.selectedFlightPlan.value))
    val state = _state.asStateFlow()


    fun onEvent(event: PilotEvent) {
        when (event) {
            NoPlan -> {
                navigator.navigate(NAV.HOME.path)
            }

        }
    }
}