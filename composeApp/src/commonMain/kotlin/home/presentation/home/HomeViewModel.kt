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
import org.mobilenativefoundation.store.store5.StoreReadResponse
import repository.FlightDateRepo
import repository.MissionRepo
import repository.domain.UserId

class HomeViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val missionRepo by inject<MissionRepo>()

    private val _state = MutableStateFlow(HomeState(dates = emptyMap(), datesLoading = emptyMap()))
    val state = _state.asStateFlow()

    init {
        loadMissions()
    }

    private fun loadMissions() {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return
        val userId = UserId(authId)
        viewModelScope.launch {
            missionRepo.getMissions(userId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> response.value.map { mission ->
                        _state.update {
                            it.copy(
                                datesLoading = it.datesLoading.plus(
                                    Pair(mission, true)
                                ),
                                loading = false
                            )
                        }
                        viewModelScope.launch {
                            flightDateRepo.getDates(mission.id).collect { dateResponse ->
                                when (dateResponse) {
                                    is StoreReadResponse.Data -> _state.update {
                                        it.copy(
                                            dates = it.dates.plus(
                                                Pair(mission, dateResponse.value)
                                            ),
                                            datesLoading = it.datesLoading.plus(
                                                Pair(mission, false)
                                            )
                                        )
                                    }

                                    is StoreReadResponse.Error.Custom<*> -> TODO()
                                    is StoreReadResponse.Error.Exception -> TODO()
                                    is StoreReadResponse.Error.Message -> TODO()
                                    StoreReadResponse.Initial -> TODO()
                                    is StoreReadResponse.Loading -> _state.update {
                                        it.copy(
                                            datesLoading = it.datesLoading.plus(
                                                Pair(mission, true)
                                            )
                                        )
                                    }

                                    is StoreReadResponse.NoNewData -> _state.update {
                                        it.copy(
                                            datesLoading = it.datesLoading.plus(
                                                Pair(mission, false)
                                            )
                                        )
                                    }
                                }

                            }
                        }
                    }

                    is StoreReadResponse.Error.Exception -> Napier.e(
                        "Mission loading error",
                        response.error
                    )

                    is StoreReadResponse.Error.Message -> Napier.e(response.message)
                    is StoreReadResponse.Loading -> _state.update { state ->
                        state.copy(loading = true)
                    }

                    is StoreReadResponse.NoNewData -> _state.update { state ->
                        state.copy(loading = false)
                    }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    StoreReadResponse.Initial -> TODO()
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
        }
    }
}