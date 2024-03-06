package planning.presentation.components.flightdate_list


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import repository.domain.NetworkFlightDate

@Composable
fun FlightDateList(dates: List<NetworkFlightDate>, onSelectDate: (date: NetworkFlightDate) -> Unit) {
    LazyColumn {
        items(dates) {
            FlightDateListItem(it, onClick = { onSelectDate(it) })
        }
    }
}

