package planning.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import friends.domain.Friend
import friends.presentation.components.FriendPhoto
import planning.presentation.domain.Mission

@Composable
fun MissionListItem(mission: Mission, modifier: Modifier) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Text(text = "${mission.id}: ${mission.description}", modifier = Modifier.weight(1f))

    }
}
