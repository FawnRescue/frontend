package planning.presentation.flightdate_editor

import planning.domain.FlightPlan
import planning.domain.InsertableFlightDate

sealed interface FlightDateEditorEvent {
    data object Cancel : FlightDateEditorEvent
    data class Save(val data: InsertableFlightDate) : FlightDateEditorEvent
}
