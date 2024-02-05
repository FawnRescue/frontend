package home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import home.presentation.home.HomeEvent.*
import planning.presentation.components.MissionListItem
import planning.presentation.components.flightdate_list.FlightDateList
import planning.presentation.components.flightdate_list.FlightDateListItem

@Composable
fun HomeScreen(onEvent: (HomeEvent) -> Unit, state: HomeState) {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                onEvent(Logout)

            }) {
                Text(text = "Logout")
            }
            IconButton(onClick = {
                onEvent(ProfileButton)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Profile"
                )
            }
        }
    }) {
        Column(modifier = Modifier.padding(it)) {
            Text("Available Flight Dates:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            LazyColumn {
                state.dates.forEach { pair ->
                    if (pair.second.isEmpty()) {
                        return@forEach
                    }
                    item {
                        MissionListItem(
                            pair.first,
                            Modifier.background(MaterialTheme.colorScheme.background)
                        )
                    }
                    items(pair.second) { date ->
                        FlightDateListItem(date, {onEvent(DateSelected(date))}, modifier = Modifier.offset(10.dp))
                        Spacer(Modifier.height(2.dp))
                    }
                    item{
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

