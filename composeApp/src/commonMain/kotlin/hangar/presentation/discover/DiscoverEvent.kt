package hangar.presentation.discover

import hangar.domain.Aircraft

sealed interface DiscoverEvent {
    object OnScanDevices : DiscoverEvent
    object OnCancelDiscovery : DiscoverEvent
    data class OnAddDrone(val aircraftName: String) : DiscoverEvent
}
