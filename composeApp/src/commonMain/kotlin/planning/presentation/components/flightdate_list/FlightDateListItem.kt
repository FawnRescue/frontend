package planning.presentation.components.flightdate_list

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import repository.domain.NetworkFlightDate

@Composable
fun FlightDateListItem(
    flightDate: NetworkFlightDate,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        headlineContent = { Text("Flight Date") },
        supportingContent = {
            Text(text = "${flightDate.start_date} - ${flightDate.end_date}")
        },
        shadowElevation = 10.dp
    )
}
