package planning.presentation.components.flightdate_list


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import repository.domain.FlightDate

@Composable
fun FlightDateListScreen(dates: List<FlightDate>, onSelectDate: (date: FlightDate) -> Unit) {
    LazyColumn {
        items(dates) {
            FlightDateListItem(it, onClick = { onSelectDate(it) })
        }
    }
}

