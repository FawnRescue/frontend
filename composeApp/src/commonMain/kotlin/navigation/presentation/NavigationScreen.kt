package navigation.presentation

import App
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
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun NavigationScreen(
    state: NavigationState,
    selectedItem: NavigationEnum,
    onEvent: (NavigationEvent) -> Unit
) {
    Scaffold(bottomBar = {
        if (selectedItem.navBar) {
            NavigationBar {
                NavigationEnum.entries.filter { it.navItem }.forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = {
                            onEvent(NavigationEvent.OnNavItemClicked(item))
                        },
                        icon = {
                            item.icon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = { Text(text = item.label) }
                    )
                }
            }
        }
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            NavHost(
                modifier = Modifier.fillMaxSize(),
                navigator = state.navigator,
                navTransition = NavTransition(),
                initialRoute = NavigationEnum.entries.first().path
            ) {

                NavigationEnum.entries.forEach {
                    when (it) {
                        NavigationEnum.HOME -> scene(
                            route = it.path,
                            navTransition = NavTransition()
                        ) {
                        }

                        NavigationEnum.FRIENDS -> scene(
                            route = it.path,
                            navTransition = NavTransition()
                        ) {
                            val viewModel = getViewModel(
                                key = "friend-list-screen",
                                factory = viewModelFactory {
                                    FriendListViewModel()
                                }
                            )
                            val stateFriend by viewModel.state.collectAsState()
                            FriendListScreen(
                                state = stateFriend,
                                newFriend = viewModel.newFriend,
                                onEvent = viewModel::onEvent
                            )
                        }

                        NavigationEnum.LOGIN -> scene(
                            route = it.path,
                            navTransition = NavTransition()
                        ) {
                            App()
                        }
                    }
                }
            }
        }
    }
}