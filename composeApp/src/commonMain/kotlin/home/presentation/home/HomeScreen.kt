package home.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.pullrefresh.pullRefresh
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import core.utils.RescueIcons
import home.presentation.home.HomeEvent.DateSelected
import home.presentation.home.HomeEvent.Logout
import home.presentation.home.HomeEvent.ProfileButton
import kotlinx.coroutines.launch
import planning.presentation.components.flightdate_list.FlightDateListItem


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(onEvent: (HomeEvent) -> Unit, state: HomeState) {
    val refreshScope = rememberCoroutineScope()
    val threshold = with(LocalDensity.current) { 160.dp.toPx() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {Text("Flight Date Overview")},
                actions = {
                    IconButton(onClick = { onEvent(ProfileButton) }) {
                        Icon(imageVector = RescueIcons.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { onEvent(Logout) }) {
                        Icon(
                            RescueIcons.Logout,
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
                .pullRefresh(onPull = {
                    when {
                        state.loading -> 0f
                        else -> {
                            val newOffset = (state.refreshCurrentDistance + it).coerceAtLeast(0f)
                            val dragConsumed = newOffset - state.refreshCurrentDistance
                            onEvent(HomeEvent.NewRefreshDistance(newOffset))
                            dragConsumed
                        }
                    }
                }, onRelease = {
                    if (state.loading) return@pullRefresh 0f // Already refreshing - don't call refresh again.
                    if (state.refreshCurrentDistance > threshold) onEvent(HomeEvent.Refresh)

                    refreshScope.launch {
                        animate(
                            initialValue = state.refreshCurrentDistance,
                            targetValue = 0f
                        ) { value, _ ->
                            onEvent(HomeEvent.NewRefreshDistance(value))
                        }
                    }

                    // Only consume if the fling is downwards and the indicator is visible
                    return@pullRefresh if (it > 0f && state.refreshCurrentDistance > 0f) {
                        it
                    } else {
                        0f
                    }

                })
        ) {

            AnimatedVisibility(visible = (state.loading || state.refreshCurrentDistance / threshold > 0)) {
                if (state.loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                } else {
                    LinearProgressIndicator(
                        state.refreshCurrentDistance / threshold,
                        Modifier.fillMaxWidth()
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (state.loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                state.dates.forEach { entry ->
                    if (entry.value.isEmpty()) {
                        return@forEach
                    }
                    item {
                        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                        Text(
                            text = entry.key.description,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                    items(entry.value) { date ->
                        FlightDateListItem(
                            date,
                            { onEvent(DateSelected(date)) },
                            modifier = Modifier.clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                        )
                        Spacer(Modifier.height(2.dp))

                    }
                }
            }
        }
    }
}

