package planning.presentation.flightdate_editor


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDateEditorScreen(onEvent: (FlightDateEditorEvent) -> Unit, state: FlightDateEditorState) {
    val openDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (openDialog.value) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = true)
    val snackState = remember { SnackbarHostState() }


    Box(propagateMinConstraints = false) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { showTimePicker = true }
        ) {
            Text("Set Time")
        }
        SnackbarHost(hostState = snackState)
    }

    if (showTimePicker) {
        Dialog(
            onDismissRequest = { showTimePicker = false },
        ) {
            Column {
                TimePicker(state = timePickerState)
                Button({ showTimePicker = false }) {
                    Text("Save Time")
                }
            }
        }
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ openDialog.value = true }) {
                Text("Select Date")
            }
            datePickerState.selectedDateMillis?.let {
                Text(Instant.fromEpochMilliseconds(it).toString())
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button({ showTimePicker = true }) {
                Text("Select Start Time")
            }
            Text("${timePickerState.hour}:${timePickerState.minute}")
        }

        Button(onClick = {
//        onEvent(FlightDateEditorEvent.Save(InsertableFlightDate(
//            id = state.selectedFlightDate?.id,
//            start_date = Instant.fromEpochMilliseconds(startDateState.selectedDateMillis),
//            end_date = Instant.fromEpochMilliseconds(endDateState.selectedDateMillis),
//        )))
        }, enabled = datePickerState.selectedDateMillis != null) {
            Text("Save")
        }
    }

}

