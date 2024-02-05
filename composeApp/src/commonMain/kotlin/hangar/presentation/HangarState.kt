package hangar.presentation

import hangar.domain.Aircraft
import hangar.domain.DroneStatus

data class HangarState(
    val aircrafts: List<Aircraft>?,
    val selectedAircraft: Aircraft?,
    val droneStatus: DroneStatus?
)