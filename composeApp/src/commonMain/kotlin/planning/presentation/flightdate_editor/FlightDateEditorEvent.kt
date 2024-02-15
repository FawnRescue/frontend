package planning.presentation.flightdate_editor

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import repository.domain.AircraftId

sealed interface FlightDateEditorEvent {
    data object Cancel : FlightDateEditorEvent
    data object Save : FlightDateEditorEvent
    data class SelectAircraft(val id: AircraftId) : FlightDateEditorEvent
    data class CloseDatePicker(val date: Instant?) : FlightDateEditorEvent
    data object OpenDatePicker : FlightDateEditorEvent
    data class CloseTimePicker(val time: LocalTime?) : FlightDateEditorEvent
    data object OpenStartTimePicker : FlightDateEditorEvent
    data object OpenEndTimePicker : FlightDateEditorEvent
}

