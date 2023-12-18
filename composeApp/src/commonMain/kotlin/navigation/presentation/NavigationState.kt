package navigation.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import moe.tlaster.precompose.navigation.Navigator

data class NavigationState(
    val selectedItem: Int = 0,
    val navigator: Navigator = Navigator()
)