package hangar.presentation

import hangar.domain.AircraftStatus
import repository.domain.Aircraft

data class HangarState(
    val aircrafts: List<Aircraft>?,
    val selectedAircraft: Aircraft?,
    val aircraftStatus: AircraftStatus?,
    val loading: Boolean = true,
)