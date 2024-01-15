package hangar.presentation.discover


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DiscoverScreen(onEvent: (DiscoverEvent) -> Unit, state: DiscoverState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { onEvent(DiscoverEvent.OnScanDevices) }) {
            Text("Scan for Drones")
        }
        state.discoveredDevices.map {
            Text(it)
        }
        Button(onClick = { onEvent(DiscoverEvent.OnCancelDiscovery) }) {
            Text("Cancel Discovery")
        }
    }

}

