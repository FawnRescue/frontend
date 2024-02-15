package planning.presentation.flightdate_editor


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDateEditorScreen(onEvent: (FlightDateEditorEvent) -> Unit, state: FlightDateEditorState) {
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)
    if (state.isDatePickerOpen) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(onDismissRequest = {
            onEvent(FlightDateEditorEvent.CloseDatePicker(null))
        }, confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        FlightDateEditorEvent.CloseDatePicker(
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
                onEvent(FlightDateEditorEvent.CloseDatePicker(null))
            }) {
                Text("Cancel")
            }
        }) {
            DatePicker(state = datePickerState)
        }
    } else if (state.isStartTimePickerOpen || state.isEndTimePickerOpen) {
        Dialog(
            onDismissRequest = {
                onEvent(FlightDateEditorEvent.CloseTimePicker(null))
            },
        ) {
            Column {
                TimePicker(state = timePickerState)
                Row {
                    Button(
                        onClick = {
                            onEvent(
                                FlightDateEditorEvent.CloseTimePicker(
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
                        onClick = { onEvent(FlightDateEditorEvent.CloseTimePicker(null)) }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
    Column(Modifier.offset(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ onEvent(FlightDateEditorEvent.OpenDatePicker) }) {
                Text("Select Date")
            }
            Spacer(Modifier.width(10.dp))
            state.date?.let {
                Text("${state.date.dayOfMonth}.${state.date.monthNumber}.${state.date.year}")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ onEvent(FlightDateEditorEvent.OpenStartTimePicker) }) {
                Text("Select Start Time")
            }
            Spacer(Modifier.width(10.dp))
            state.startTime?.let {
                Text("${state.startTime.hour.toString().padStart(2, '0')}:${state.startTime.minute.toString().padStart(2, '0')}")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ onEvent(FlightDateEditorEvent.OpenEndTimePicker) }) {
                Text("Select End Time")
            }
            Spacer(Modifier.width(10.dp))
            state.endTime?.let {
                Text("${state.endTime.hour.toString().padStart(2, '0')}:${state.endTime.minute.toString().padStart(2, '0')}")
            }
        }
        Button(onClick = {
            onEvent(FlightDateEditorEvent.Save)
        }, enabled = state.isSaveEnabled) {
            Text("Save")
        }
        Button(onClick = {
            onEvent(FlightDateEditorEvent.Cancel)
        }) {
            Text("Cancel")
        }
    }

}

