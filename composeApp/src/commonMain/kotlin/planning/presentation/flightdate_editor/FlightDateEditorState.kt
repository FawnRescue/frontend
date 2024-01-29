package planning.presentation.flightdate_editor

import planning.domain.EditableFlightDate
import planning.domain.FlightDate

data class FlightDateEditorState(
    val editedFlightDate: EditableFlightDate?,
    val selectedFlightDate: FlightDate?,
)