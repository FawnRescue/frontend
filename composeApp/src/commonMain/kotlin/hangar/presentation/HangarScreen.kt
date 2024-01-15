package hangar.presentation


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import friends.presentation.FriendListEvent

@Composable
fun HangarScreen(onEvent: (HangarEvent) -> Unit, state: HangarState) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onEvent(HangarEvent.AddAircraft)
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add aircraft"
            )
        }
    }) {

    }
}
