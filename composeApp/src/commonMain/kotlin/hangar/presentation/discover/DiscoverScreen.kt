package hangar.presentation.discover


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DiscoverScreen(onEvent: (DiscoverEvent) -> Unit, state: DiscoverState) {
    Column {
        Button(onClick = { onEvent(DiscoverEvent.OnScanDevices) }) {
            Text("Scan for Drones")
        }
        state.discoveredDevices.map {
            Text(it)
        }
    }

}

