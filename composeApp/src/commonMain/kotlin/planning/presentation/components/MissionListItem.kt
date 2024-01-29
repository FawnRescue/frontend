package planning.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import planning.domain.Mission

@Composable
fun MissionListItem(mission: Mission, modifier: Modifier, onAddFlightDate: (() -> Unit)? = null) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Text(text = mission.description, modifier = Modifier.weight(1f))
        Spacer(Modifier)
        onAddFlightDate?.let {
            Button(
                onClick = it,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Set a Date")
                }
            }
        }

    }
    Divider(modifier.fillMaxWidth())
}
