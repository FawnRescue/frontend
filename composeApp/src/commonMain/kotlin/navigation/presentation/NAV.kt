package navigation.presentation

import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.ui.graphics.vector.ImageVector
import core.utils.RescueIcons

enum class NAV(
    val label: String,
    val path: String,
    val icon: ImageVector?,
    val navBar: Boolean = true,
    val navItem: Boolean = true,
) {
    LOGIN("Login", "/login", null, false, false),
    GROUP("Group", "/group", RescueIcons.Groups),
    PLANNING("Planning", "/planning", RescueIcons.NoteAdd),
    MISSION_EDITOR("Mission Editor", "/planning/mission_editor", null, false, false),
    FLIGHT_PLAN_EDITOR("Flight Plan Editor", "/planning/flight_plan_editor", null, false, false),
    FLIGHT_DATE_EDITOR("Flight Date Editor", "/planning/flight_date_editor", null, false, false),
    HOME("Home", "/home", RescueIcons.Home),
    FRIENDS("Friends", "/friends", RescueIcons.Group),
    HANGAR("Hangar", "/hangar", RescueIcons.Flight),
    HANGAR_DISCOVER("Discover Aircraft", "/hangar/discover", null, false, false),
    PROFILE("Profile", "/profile", null, false, false),
    PILOT("Pilot", "/pilot", null, false, false),
    FLIGHT_DATE_VIEWER("Flight Date Viewer", "/planning/flight_date_viewer", null, false, false);
}
