package home.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.presentation.home.HomeEvent.DateSelected
import home.presentation.home.HomeEvent.Logout
import home.presentation.home.HomeEvent.ProfileButton
import planning.presentation.components.flightdate_list.FlightDateListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onEvent: (HomeEvent) -> Unit, state: HomeState) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { },
                actions = {
                    IconButton(onClick = { onEvent(ProfileButton) }) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Profile"
                        )
                    }
                    IconButton(onClick = { onEvent(Logout) }) {
                        Icon(
                            Icons.Rounded.Logout,
                            contentDescription = "Logout"
                        )
                    }
                })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Available Flight Dates:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (state.loading && state.datesLoading.isEmpty()) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                state.datesLoading.forEach { entry ->
                    if (!entry.value && state.dates.containsKey(entry.key) && state.dates[entry.key]!!.isEmpty()) {
                        return@forEach
                    }
                    item {
                        Text(
                            text = entry.key.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                    if (entry.value || !state.dates.containsKey(entry.key)) {
                        item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                    } else {
                        items(state.dates[entry.key]!!) { date ->
                            FlightDateListItem(
                                date,
                                { onEvent(DateSelected(date)) },
                                modifier = Modifier.offset(10.dp)
                            )
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

