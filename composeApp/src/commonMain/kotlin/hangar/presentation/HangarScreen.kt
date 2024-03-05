package hangar.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hangar.presentation.components.BatteryIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HangarScreen(onEvent: (HangarEvent) -> Unit, state: HangarState) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onEvent(HangarEvent.AddAircraft)
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add aircraft"
            )
        }
    }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = "My aircrafts (${state.aircrafts?.size ?: 0})",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold
                )
            }
            if (state.loading) {
                item {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
            state.aircrafts?.let {
                items(it) { aircraft ->
                    ListItem(
                        modifier = Modifier.clickable {
                            onEvent(
                                HangarEvent.OnSelectAircraft(
                                    aircraft
                                )
                            )
                        },
                        headlineContent = { Text(text = aircraft.name) },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Flight,
                                contentDescription = aircraft.name,
                                modifier = Modifier.size(25.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                    )

                }
            }
        }

        if (state.selectedAircraft != null) {
            Dialog(onDismissRequest = { onEvent(HangarEvent.OnDismissDialog) }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.selectedAircraft.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.droneStatus != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    BatteryIndicator(
                                        batteryPercentage = state.droneStatus.battery?.remainingPercent
                                            ?: 0f,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Icon(Icons.Default.Flight, contentDescription = "State")
                                        Text(" State: ${state.droneStatus.state}")
                                    }
                                    Row {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = "Location"
                                        )
                                        Text(" Location: ${state.droneStatus.location?.latitude}, ${state.droneStatus.location?.longitude}")
                                    }
                                    // Displaying the altitude if available
                                    state.droneStatus.altitude?.let {
                                        Row {
                                            Icon(
                                                Icons.Default.Terrain,
                                                contentDescription = "Altitude"
                                            )
                                            Text(" Altitude: ${it}m")
                                        }
                                    }
                                    // Displaying the number of satellites if available
                                    state.droneStatus.numSatellites?.let {
                                        Row {
                                            Icon(
                                                Icons.Default.Satellite,
                                                contentDescription = "Number of Satellites"
                                            )
                                            Text(" Satellites: $it")
                                        }
                                    }
                                }
                            }
                        } else {
                            Text("No Data available")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onEvent(HangarEvent.OnDeleteAircraft) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Delete aircraft")
                        }
                    }
                }
            }

        }

    }
}
