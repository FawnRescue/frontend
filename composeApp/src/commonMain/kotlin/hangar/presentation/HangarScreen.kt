package hangar.presentation


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hangar.presentation.components.DroneDetailDialog

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
            DroneDetailDialog(
                state.selectedAircraft,
                state.editableAircraft,
                state.aircraftStatus,
                onDismissRequest = { onEvent(HangarEvent.OnDismissDialog) },
                onDeleteAircraft = { onEvent(HangarEvent.OnDeleteAircraft) },
                onFOVChange = { fov -> onEvent(HangarEvent.OnFOVChange(fov))},
                onFlightHeightChange = { height -> onEvent(HangarEvent.OnFlightHeightChange(height))},
                onEditAircraft = {onEvent(HangarEvent.OnEditAircraft)},
                onSaveAircraft = {onEvent(HangarEvent.OnSaveAircraft)},
                editable = state.editable
            )

        }

    }
}
