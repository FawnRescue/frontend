package home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    if (state.dates[entry.key]?.isEmpty() == true) {
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
                            FlightDateListItem(date, {}, modifier = Modifier.offset(10.dp))
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}

