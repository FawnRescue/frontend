package planning.presentation.flightdate_editor

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import repository.domain.Aircraft
import repository.domain.AircraftId
import repository.domain.NetworkFlightDate

data class FlightDateEditorState(
    val selectedFlightDate: NetworkFlightDate?,
    val date: LocalDate?,
    val isDatePickerOpen: Boolean,
    val isStartTimePickerOpen: Boolean,
    val startTime: LocalTime?,
    val isEndTimePickerOpen: Boolean,
    val endTime: LocalTime?,
    val isSaveEnabled: Boolean,
    val aircraftId: AircraftId?,
    val aircrafts: List<Aircraft> = emptyList(),
    val loading: Boolean = true,
)