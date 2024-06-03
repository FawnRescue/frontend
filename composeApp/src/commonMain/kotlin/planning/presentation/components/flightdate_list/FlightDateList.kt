package planning.presentation.components.flightdate_list


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import repository.domain.NetworkFlightDate

@Composable
fun FlightDateList(
    dates: List<NetworkFlightDate>,
    onSelectDate: ((date: NetworkFlightDate) -> Unit)?,
) {
    val editable = onSelectDate != null
    var modifier = Modifier.clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
    if (!editable) {
        modifier = modifier.border(
            4.dp,
            MaterialTheme.colorScheme.secondaryContainer,
            RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
        )
    }
    LazyColumn {
        items(dates) {
            FlightDateListItem(
                it,
                onClick = if (editable) ({ onSelectDate?.invoke(it) }) else null,
                backgroundColor = if (editable) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                modifier = modifier
            )
            Spacer(Modifier.height(2.dp))
        }
    }
}

