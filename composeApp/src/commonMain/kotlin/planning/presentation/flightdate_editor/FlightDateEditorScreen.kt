package planning.presentation.flightdate_editor


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import planning.presentation.flightdate_editor.FlightDateEditorEvent.CloseDatePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.CloseTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.SelectAircraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDateEditorScreen(onEvent: (FlightDateEditorEvent) -> Unit, state: FlightDateEditorState) {
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)
    if (state.loading) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        return
    }
    val padding = 16.dp

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Flight Date and Time") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = padding),
            verticalArrangement = Arrangement.spacedBy(padding)
        ) {
            Text(
                "Please select the date and time for your flight:",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = { onEvent(FlightDateEditorEvent.OpenDatePicker) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Date")
            }
            state.date?.let {
                Text(
                    "Selected Date: ${it.dayOfMonth}.${it.monthNumber}.${it.year}",
                    Modifier.padding(start = padding)
                )
            }

            Button(
                onClick = { onEvent(FlightDateEditorEvent.OpenStartTimePicker) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Start Time")
            }
            state.startTime?.let {
                Text(
                    "Start Time: ${it.hour.toString().padStart(2, '0')}:${
                        it.minute.toString().padStart(2, '0')
                    }", Modifier.padding(start = padding)
                )
            }

            Button(
                onClick = { onEvent(FlightDateEditorEvent.OpenEndTimePicker) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select End Time")
            }
            state.endTime?.let {
                Text(
                    "End Time: ${it.hour.toString().padStart(2, '0')}:${
                        it.minute.toString().padStart(2, '0')
                    }", Modifier.padding(start = padding)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Selected Aircraft: ${state.aircraftId ?: "None"}")
                    LazyColumn {
                        if (state.aircrafts.isEmpty()) {
                            item { Text("No Aircraft in Hangar") }
                        }
                        items(state.aircrafts) {
                            SelectableItem(it.token == state.aircraftId, onItemSelected = {
                                onEvent(
                                    SelectAircraft(it.token)
                                )
                            }) {
                                Text(it.name)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(padding))

            Button(
                onClick = { onEvent(FlightDateEditorEvent.GotoFlightDateViewer) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Mission Details")
            }

            Spacer(Modifier.height(padding))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = { onEvent(FlightDateEditorEvent.Cancel) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(padding))
                ElevatedButton(
                    onClick = { onEvent(FlightDateEditorEvent.Save) },
                    enabled = state.isSaveEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }

            }
        }
    }


    // Handle DatePicker and TimePicker dialogs outside the Scaffold for better overlay handling
    if (state.isDatePickerOpen) {
        DatePickerDialogComponent(onEvent, datePickerState)
    }
    if (state.isStartTimePickerOpen || state.isEndTimePickerOpen) {
        TimePickerDialogComponent(onEvent, timePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogComponent(
    onEvent: (FlightDateEditorEvent) -> Unit,
    datePickerState: DatePickerState,
) {
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(onDismissRequest = {
        onEvent(CloseDatePicker(null))
    }, confirmButton = {
        TextButton(
            onClick = {
                onEvent(
                    CloseDatePicker(
                        datePickerState.selectedDateMillis?.let {
                            Instant.fromEpochMilliseconds(
                                it
                            )
                        }
                    )
                )

            }, enabled = confirmEnabled.value
        ) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onEvent(CloseDatePicker(null))
        }) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogComponent(
    onEvent: (FlightDateEditorEvent) -> Unit,
    timePickerState: TimePickerState,
) {
    Dialog(
        onDismissRequest = {
            onEvent(CloseTimePicker(null))
        },
    ) {
        Card {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Button(
                        onClick = { onEvent(CloseTimePicker(null)) }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onEvent(
                                CloseTimePicker(
                                    LocalTime(
                                        timePickerState.hour,
                                        timePickerState.minute
                                    )
                                )
                            )
                        }
                    ) {
                        Text("Save Time")
                    }
                }
            }
        }
    }
}


@Composable
fun SelectableItem(
    selected: Boolean,
    onItemSelected: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min)
            .background(rowColor(selected))
            .clickable {
                onItemSelected()
            }
    ) {
        content()
    }
}

@Composable
fun rowColor(selected: Boolean): Color {
    return if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        Color.Transparent
    }
}