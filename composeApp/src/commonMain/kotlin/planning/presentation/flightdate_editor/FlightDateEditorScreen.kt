package planning.presentation.flightdate_editor


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
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
import planning.presentation.flightdate_editor.FlightDateEditorEvent.Cancel
import planning.presentation.flightdate_editor.FlightDateEditorEvent.CloseDatePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.CloseTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.OpenDatePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.OpenEndTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.OpenStartTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.Save
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
    if (state.isDatePickerOpen) {
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
    } else if (state.isStartTimePickerOpen || state.isEndTimePickerOpen) {
        Dialog(
            onDismissRequest = {
                onEvent(CloseTimePicker(null))
            },
        ) {
            Column {
                TimePicker(state = timePickerState)
                Row {
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
                    Button(
                        onClick = { onEvent(CloseTimePicker(null)) }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
    Column(Modifier.offset(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ onEvent(OpenDatePicker) }) {
                Text("Select Date")
            }
            Spacer(Modifier.width(10.dp))
            state.date?.let {
                Text("${state.date.dayOfMonth}.${state.date.monthNumber}.${state.date.year}")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ onEvent(OpenStartTimePicker) }) {
                Text("Select Start Time")
            }
            Spacer(Modifier.width(10.dp))
            state.startTime?.let {
                Text(
                    "${
                        state.startTime.hour.toString().padStart(2, '0')
                    }:${state.startTime.minute.toString().padStart(2, '0')}"
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ onEvent(OpenEndTimePicker) }) {
                Text("Select End Time")
            }
            Spacer(Modifier.width(10.dp))
            state.endTime?.let {
                Text(
                    "${
                        state.endTime.hour.toString().padStart(2, '0')
                    }:${state.endTime.minute.toString().padStart(2, '0')}"
                )
            }
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
        Button(onClick = {
            onEvent(Save)
        }, enabled = state.isSaveEnabled) {
            Text("Save")
        }
        Button(onClick = {
            onEvent(Cancel)
        }) {
            Text("Cancel")
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

