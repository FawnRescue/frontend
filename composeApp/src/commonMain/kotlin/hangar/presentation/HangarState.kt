package hangar.presentation

import hangar.domain.DroneStatus
import repository.domain.Aircraft

data class HangarState(
    val aircrafts: List<Aircraft>?,
    val selectedAircraft: Aircraft?,
    val droneStatus: DroneStatus?,
    val loading: Boolean = true
)