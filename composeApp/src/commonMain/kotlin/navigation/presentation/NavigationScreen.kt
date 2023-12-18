package navigation.presentation

import App
import AppFriend
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.koin.core.qualifier.qualifier

@Composable
fun NavigationScreen(state: NavigationState, onEvent: (NavigationEvent) -> Unit) {
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
                    App()
                }

                NavigationEnum.FRIENDS -> scene(
                    route = it.path,
                    navTransition = NavTransition()
                ) {
                    AppFriend()
                }
            }
        }
    }
    NavigationBar {
        NavigationEnum.entries.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = state.selectedItem == index,
                onClick = {
                    onEvent(NavigationEvent.OnNavItemClicked(index))
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) }
            )
        }

    }
}