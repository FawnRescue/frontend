package navigation.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationEnum(
    val label: String,
    val path: String,
    val icon: ImageVector?,
    val navBar: Boolean = true,
    val navItem: Boolean = true
) {
    LOGIN("Login", "/login", null, false, false),
    GROUP("Group", "/group", Icons.Rounded.Groups),
    PLANNING("Planning", "/planning", Icons.Rounded.NoteAdd),
    MISSION_EDITOR("Mission Editor", "/planning/mission_editor", null, false ,false),
    FLIGHT_PLAN_EDITOR("Flight Plan Editor", "/planning/flight_plan_editor", null, false ,false),
    HOME("Home", "/home", Icons.Rounded.Home),
    FRIENDS("Friends", "/friends", Icons.Rounded.Group),
    HANGAR("Hangar", "/hangar", Icons.Rounded.Flight);
}