package planning.presentation.flightdate_editor

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import repository.domain.FlightDate

data class FlightDateEditorState (
    val selectedFlightDate: FlightDate?,
    val date: LocalDate?,
    val isDatePickerOpen: Boolean,
    val isStartTimePickerOpen: Boolean,
    val startTime: LocalTime?,
    val isEndTimePickerOpen: Boolean,
    val endTime: LocalTime?,
    val isSaveEnabled: Boolean
)