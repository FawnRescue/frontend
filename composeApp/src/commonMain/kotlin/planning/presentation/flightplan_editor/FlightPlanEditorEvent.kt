package planning.presentation.flightplan_editor

sealed interface FlightPlanEditorEvent {
    object SampleEvent : FlightPlanEditorEvent
    data class SampleEventWithData(val data: Unit) : FlightPlanEditorEvent
}
