package home.presentation.home

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import home.presentation.home.HomeEvent.Logout
import home.presentation.home.HomeEvent.ProfileButton
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
import repository.FlightDateRepo
import repository.MissionKey
import repository.MissionRepo
import repository.domain.Mission

class HomeViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val missionRepo by inject<MissionRepo>()

    private val _state = MutableStateFlow(HomeState(dates = emptyMap()))
    val state = _state.asStateFlow()

    init {
        loadMissions(false)
    }

    private fun loadMissions(refresh: Boolean) {
        viewModelScope.launch {
            missionRepo.store.stream(
                StoreReadRequest.cached(
                    MissionKey.Read.ByOwner, refresh
                )
            ).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.map { mission ->
                        viewModelScope.launch {
                            loadDates(mission, response.value.size)
                        }
                    }

                    is StoreReadResponse.Error.Exception -> Napier.e(
                        "Mission loading error",
                        response.error
                    )

                    is StoreReadResponse.Error.Message -> Napier.e(response.message)
                    is StoreReadResponse.Loading -> {}
                    is StoreReadResponse.NoNewData -> {}
                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    StoreReadResponse.Initial -> TODO()
                }
            }

        }
    }

    private fun loadDates(mission: Mission, missionAmount: Int) {
        viewModelScope.launch {
            flightDateRepo.getDates(mission.id).collect { dateResponse ->
                when (dateResponse) {
                    is StoreReadResponse.Data -> _state.update {
                        it.copy(
                            dates = it.dates.plus(
                                Pair(mission, dateResponse.value)
                            ),
                            loading = (it.dates.size + 1) < missionAmount
                        )
                    }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    is StoreReadResponse.Error.Exception -> TODO()
                    is StoreReadResponse.Error.Message -> TODO()
                    StoreReadResponse.Initial -> TODO()
                    is StoreReadResponse.Loading -> {}
                    is StoreReadResponse.NoNewData -> {}
                }

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

            is HomeEvent.DateSelected -> flightDateRepo.selectedFlightDate.update { event.date }
                .also {
                    navigator.navigate(NAV.PILOT.path)
                }

            is HomeEvent.NewRefreshDistance -> {
                _state.update {
                    it.copy(
                        refreshCurrentDistance = event.distance
                    )
                }
            }

            HomeEvent.Refresh -> {
                _state.update { it.copy(loading = true) }
                loadMissions(true)
            }
        }
    }
}