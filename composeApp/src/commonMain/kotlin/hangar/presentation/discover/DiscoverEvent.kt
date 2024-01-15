package hangar.presentation.discover

sealed interface DiscoverEvent {
    object OnScanDevices : DiscoverEvent
}
