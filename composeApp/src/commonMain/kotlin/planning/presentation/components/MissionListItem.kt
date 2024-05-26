package planning.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import repository.domain.Mission


@Composable
fun MissionListItem(
    mission: Mission,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    modifier: Modifier = Modifier,
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
        ),
        modifier = modifier.clip(
            RoundedCornerShape(8.dp)
        ),
        leadingContent = {
            Icon(
                imageVector = Icons.Default.PinDrop,
                contentDescription = "Mission",
                modifier = Modifier.size(24.dp)
            )
        },
        headlineContent = {
            Text(mission.description, style = MaterialTheme.typography.headlineSmall)
        }
    )
}
