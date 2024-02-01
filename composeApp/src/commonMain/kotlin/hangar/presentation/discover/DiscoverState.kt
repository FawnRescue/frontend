package hangar.presentation.discover

data class DiscoverState(
    val discoveredDevices: List<BluetoothDevice> = emptyList(),
    val isScanning: Boolean = false,
    val percentTransmitted: Float = 0f,
    val selectedDrone: String? = null,
    val otp: String = ""
)