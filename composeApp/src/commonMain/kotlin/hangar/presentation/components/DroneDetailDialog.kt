package hangar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hangar.domain.AircraftStatus
import pilot.OSD
import repository.domain.Aircraft

@Composable
fun DroneDetailDialog(
    aircraft: Aircraft,
    editableAircraft: Aircraft?,
    status: AircraftStatus?,
    editable: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteAircraft: () -> Unit,
    onEditAircraft: () -> Unit,
    onSaveAircraft: () -> Unit,
    onFOVChange: (cameraFov: Double) -> Unit,
    onFlightHeightChange: (flightHeight: Double) -> Unit,
) {
    Dialog(onDismissRequest) {
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
                    text = aircraft.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (status != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        OSD(status)
                    }
                } else {
                    Text("No Data available")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (editable && editableAircraft != null) {
                    OutlinedTextField(
                        value = editableAircraft.cameraFOV.toString(),
                        onValueChange = { value ->
                            val fov = value.toDoubleOrNull()
                            if (fov != null) {
                                onFOVChange(fov)
                            }
                        },
                        label = { Text("Enter Camera FOV") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    OutlinedTextField(
                        value = editableAircraft.flightHeight.toString(),
                        onValueChange = { value ->
                            val height = value.toDoubleOrNull()
                            if (height != null) {
                                onFlightHeightChange(height)
                            }
                        },
                        label = { Text("Enter Flight Height") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                } else {
                    TextField(
                        readOnly = true,
                        onValueChange = {},
                        label = { Text("Camera FOV") },
                        value = aircraft.cameraFOV.toString()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        readOnly = true,
                        onValueChange = {},
                        label = { Text("Flight Height") },
                        value = aircraft.flightHeight.toString()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Button(
                        onClick = onDeleteAircraft,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Delete aircraft")
                    }
                    if (editable) {
                        if (editableAircraft == null) {
                            Button(
                                onClick = onEditAircraft,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Edit aircraft properties")
                            }
                        } else {
                            Button(
                                onClick = onSaveAircraft,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Save aircraft properties")
                            }
                        }
                    }
                }
            }
        }
    }
}