package home.presentation.home

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import home.presentation.home.HomeEvent.*
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
import repository.FlightDateRepo
import repository.MissionRepo

class HomeViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val missionRepo by inject<MissionRepo>()

    private val _state = MutableStateFlow(HomeState(dates = emptyList()))
    val state = _state.asStateFlow()

    init {
        loadDates()
    }

    private fun loadDates() {
        viewModelScope.launch {
            val missions = missionRepo.getMissions()
            _state.update { state ->
                state.copy(dates = missions.map {
                    Pair(it, flightDateRepo.getDates(it.id))
                })
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            Logout -> {
                viewModelScope.launch {
                    supabase.auth.signOut()
                    navigator.navigate(NAV.LOGIN.path)
                }
            }

            ProfileButton -> {
                navigator.navigate(NAV.PROFILE.path)

            }

            is DateSelected -> flightDateRepo.selectedFlightDate.update { event.date }.also {
                navigator.navigate(NAV.PILOT.path)
            }
        }
    }
}