package hangar.presentation.discover

sealed interface DiscoverEvent {
    object OnScanDevices : DiscoverEvent
    object OnCancelDiscovery : DiscoverEvent
    data class OnSelectDrone(val address: String) : DiscoverEvent
    object OnAddDrone : DiscoverEvent
    object OnCancelAddDrone : DiscoverEvent
    data class OnChangeOTP(val otp: String) : DiscoverEvent
}
