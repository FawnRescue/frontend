package pilot

sealed interface PilotEvent {
    data object NoPlan : PilotEvent
}
