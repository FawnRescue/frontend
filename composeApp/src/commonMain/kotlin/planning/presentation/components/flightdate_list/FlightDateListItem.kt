package planning.presentation.components.flightdate_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import repository.domain.NetworkFlightDate

fun formatDate(date: LocalDateTime): String {
    return "${date.dayOfMonth.toString().padStart(2, '0')}.${
        date.monthNumber.toString().padStart(2, '0')
    }.${date.year.toString().padStart(4, '0')} ${
        date.hour.toString().padStart(2, '0')
    }:${date.minute.toString().padStart(2, '0')}"
}

@Composable
fun FlightDateListItem(
    flightDate: NetworkFlightDate,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val date = flightDate.start_date.toLocalDateTime(TimeZone.currentSystemDefault())
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        leadingContent = {
            Icon(
                imageVector = Icons.Default.FlightTakeoff,
                contentDescription = "Flight Date",
                modifier = Modifier.size(24.dp)
            )
        },
        headlineContent = {
            Text(
                formatDate(date),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        tonalElevation = 8.dp,
        shadowElevation = 25.dp
    )
}
