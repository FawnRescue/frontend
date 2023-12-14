package navigation.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationEnum(val label: String, val path: String, val icon: ImageVector) {
    HOME("Home", "/home", Icons.Rounded.Home),
    FRIENDS("Friends", "/friends", Icons.Rounded.Group);
}