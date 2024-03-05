package planning.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import repository.domain.Mission


@Composable
fun MissionListItem(mission: Mission, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            // Optional: Icon based on mission's status or other criteria
            Icon(
                imageVector = Icons.Default.Flight, // Choose an icon relevant to your application
                contentDescription = "Mission",
                modifier = Modifier.size(24.dp)
            )
        },
        overlineContent = {
            // Displaying the creation date of the mission
            Text(
                text = "Created: ${mission.created_at}",
                style = MaterialTheme.typography.labelSmall
            )
        },
        headlineContent = {
            // Main content: mission description
            Text(mission.description, style = MaterialTheme.typography.bodyMedium)
        }
    )
}
