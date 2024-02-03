package navigation.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import friends.presentation.FriendListScreen
import friends.presentation.FriendListViewModel
import hangar.presentation.HangarScreen
import hangar.presentation.HangarViewModel
import hangar.presentation.discover.DiscoverScreen
import hangar.presentation.discover.DiscoverViewModel
import home.presentation.home.HomeScreen
import home.presentation.home.HomeViewModel
import login.presentation.LoginScreen
import login.presentation.LoginViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import navigation.presentation.NAV.FLIGHT_DATE_EDITOR
import navigation.presentation.NAV.FLIGHT_PLAN_EDITOR
import navigation.presentation.NAV.FRIENDS
import navigation.presentation.NAV.GROUP
import navigation.presentation.NAV.HANGAR
import navigation.presentation.NAV.HANGAR_DISCOVER
import navigation.presentation.NAV.HOME
import navigation.presentation.NAV.LOGIN
import navigation.presentation.NAV.MISSION_EDITOR
import navigation.presentation.NAV.PLANNING
import navigation.presentation.NAV.PROFILE
import org.koin.compose.koinInject
import planning.presentation.flightdate_editor.FlightDateEditorScreen
import planning.presentation.flightdate_editor.FlightDateEditorViewModel
import planning.presentation.flightplan_editor.FlightPlanEditorScreen
import planning.presentation.flightplan_editor.FlightPlanEditorViewModel
import planning.presentation.mission_editor.MissionEditorScreen
import planning.presentation.mission_editor.MissionEditorViewModel
import planning.presentation.mission_list.MissionListScreen
import planning.presentation.mission_list.MissionListViewModel
import profile.ProfileEditorScreen
import profile.ProfileEditorViewModel

@Composable
fun NavigationScreen(
    selectedItem: NAV,
    onEvent: (NavigationEvent) -> Unit,
) {
    val navigator = koinInject<Navigator>()
    Scaffold(bottomBar = {
        if (selectedItem.navBar) {
            NavigationBar {
                NAV.entries.filter { it.navItem }.forEach { item ->
                    NavigationBarItem(selected = selectedItem == item, onClick = {
                        onEvent(NavigationEvent.OnNavItemClicked(item))
                    }, icon = {
                        item.icon?.let {
                            Icon(
                                imageVector = it, contentDescription = item.label
                            )
                        }
                    }, label = { Text(text = item.label) })
                }
            }
        }
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                modifier = Modifier.fillMaxSize(),
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = NAV.entries.first().path
            ) {

                NAV.entries.forEach {
                    when (it) {
                        HOME -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel =
                                getViewModel(key = "home-screen", factory = viewModelFactory {
                                    HomeViewModel()
                                })
                            val stateHome by viewModel.state.collectAsState()
                            HomeScreen(
                                state = stateHome, onEvent = viewModel::onEvent
                            )
                        }

                        FRIENDS -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(
                                key = "friend-list-screen",
                                factory = viewModelFactory {
                                    FriendListViewModel()
                                })
                            val stateFriend by viewModel.state.collectAsState()
                            FriendListScreen(
                                state = stateFriend, onEvent = viewModel::onEvent
                            )
                        }

                        LOGIN -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel =
                                getViewModel(key = "login-screen", factory = viewModelFactory {
                                    LoginViewModel()
                                })
                            val stateLogin by viewModel.state.collectAsState()
                            LoginScreen(
                                state = stateLogin, onEvent = viewModel::onEvent
                            )
                        }

                        PLANNING -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(
                                key = "mission-list-screen",
                                factory = viewModelFactory {
                                    MissionListViewModel()
                                })
                            val stateMissionList by viewModel.state.collectAsState()
                            MissionListScreen(
                                state = stateMissionList, onEvent = viewModel::onEvent
                            )
                        }

                        MISSION_EDITOR -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(
                                key = "mission-editor-screen",
                                factory = viewModelFactory {
                                    MissionEditorViewModel()
                                })
                            val stateMissionEditor by viewModel.state.collectAsState()
                            MissionEditorScreen(
                                state = stateMissionEditor, onEvent = viewModel::onEvent
                            )
                        }

                        FLIGHT_PLAN_EDITOR -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(
                                key = "flight-plan-editor-screen",
                                factory = viewModelFactory {
                                    FlightPlanEditorViewModel()
                                })
                            val stateFlightPlanEditor by viewModel.state.collectAsState()
                            FlightPlanEditorScreen(
                                state = stateFlightPlanEditor, onEvent = viewModel::onEvent
                            )
                        }

                        GROUP -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {

                        }

                        HANGAR -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel =
                                getViewModel(key = "hangar-screen", factory = viewModelFactory {
                                    HangarViewModel()
                                })
                            val stateHangar by viewModel.state.collectAsState()
                            HangarScreen(
                                state = stateHangar, onEvent = viewModel::onEvent
                            )
                        }

                        FLIGHT_DATE_EDITOR -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(
                                key = "flight-date-editor-screen",
                                factory = viewModelFactory {
                                    FlightDateEditorViewModel()
                                })
                            val stateFlightDateEditor by viewModel.state.collectAsState()
                            FlightDateEditorScreen(
                                state = stateFlightDateEditor, onEvent = viewModel::onEvent
                            )
                        }

                        PROFILE -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel =
                                getViewModel(key = "profile-screen", factory = viewModelFactory {
                                    ProfileEditorViewModel()
                                })
                            val stateProfile by viewModel.state.collectAsState()
                            ProfileEditorScreen(
                                state = stateProfile, onEvent = viewModel::onEvent
                            )
                        }

                        HANGAR_DISCOVER -> scene(
                            route = it.path, navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(key = "hangar-discover-screen",
                                factory = viewModelFactory {
                                    DiscoverViewModel()
                                })
                            val stateHangarDiscover by viewModel.state.collectAsState()
                            DiscoverScreen(
                                state = stateHangarDiscover, onEvent = viewModel::onEvent
                            )
                        }
                    }
                }
            }
        }
    }
}