package navigation.presentation

import App
import AppFriend
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun NavigationScreen(
    state: NavigationState,
    selectedItem: NavigationEnum,
    onEvent: (NavigationEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                        AppFriend()
                    }

                    NavigationEnum.FRIENDS -> scene(
                        route = it.path,
                        navTransition = NavTransition()
                    ) {
                        AppFriend()
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
        if (selectedItem.navBar) {
            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
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
    }
}