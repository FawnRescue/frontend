package planning.presentation.components.flightdate_list

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import planning.domain.FlightDate
@Composable
fun FlightDateListItem(
    flightDate: FlightDate,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable { onClick() },
        headlineContent = { Text("Flight Date") },
        supportingContent = {
            Text(text = "${flightDate.start_date} - ${flightDate.end_date}")
        },
        shadowElevation = 5.dp
    )
}
