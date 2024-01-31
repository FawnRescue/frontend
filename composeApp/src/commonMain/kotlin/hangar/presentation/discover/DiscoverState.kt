package hangar.presentation.discover

data class DiscoverState(
    val discoveredDevices: List<BluetoothDevice>,
    val isScanning: Boolean = false,
    val percentTransmitted: Float = 0f
)