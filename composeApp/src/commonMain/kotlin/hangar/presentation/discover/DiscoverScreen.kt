package hangar.presentation.discover


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun DiscoverScreen(onEvent: (DiscoverEvent) -> Unit, state: DiscoverState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header Text
        Text(
            text = "Discover Drones",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scan for Drones button
        Button(
            onClick = { onEvent(DiscoverEvent.OnScanDevices) },
            enabled = !state.isScanning,
            modifier = Modifier.padding(8.dp)
        ) {
            if (state.isScanning) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Text("Scanning...", Modifier.padding(start = 8.dp))
            } else {
                Text("Scan for Drones")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of discovered devices
        LazyColumn {
            items(state.discoveredDevices) { device ->
                ListItem(
                    headlineContent = { Text(device.name) },
                    leadingContent = { Icon(Icons.Default.DeviceHub, contentDescription = null) },
                    modifier = Modifier.clickable(
                        enabled = state.percentTransmitted == -1f,
                        onClick = { onEvent(DiscoverEvent.OnSelectDrone(device.address)) }
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cancel Discovery button
        Button(onClick = { onEvent(DiscoverEvent.OnCancelDiscovery) }) {
            Text("Cancel Discovery")
        }
    }
    if (state.selectedDrone != null) {
        Dialog(onDismissRequest = {}) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Please enter the OTP you received via E-Mail")
                    OutlinedTextField(
                        value = state.otp,
                        onValueChange = { onEvent(DiscoverEvent.OnChangeOTP(it)) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // Transmission Progress
                    if (state.percentTransmitted != -1f) {
                        LinearProgressIndicator(
                            progress = state.percentTransmitted,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                        Text("Transmission in progress: ${(state.percentTransmitted * 100).toInt()}%")
                    }
                    Button(onClick = { onEvent(DiscoverEvent.OnAddDrone) }) {
                        Text("Add Drone")
                    }
                    Button(onClick = { onEvent(DiscoverEvent.OnCancelAddDrone) }) {
                        Text("Cancel Add")
                    }
                }
            }
        }
    }
}
