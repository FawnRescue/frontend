package hangar.presentation.discover

sealed interface DiscoverEvent {
    object SampleEvent : DiscoverEvent
    data class SampleEventWithData(val data: Unit) : DiscoverEvent
}
