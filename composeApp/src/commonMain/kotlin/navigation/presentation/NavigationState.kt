package navigation.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import moe.tlaster.precompose.navigation.Navigator

data class NavigationState(
    val navigator: Navigator = Navigator()
)