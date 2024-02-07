package hangar.presentation

import repository.domain.Aircraft
import hangar.domain.DroneStatus
import org.jetbrains.compose.resources.LoadState

data class HangarState(
    val aircrafts: List<Aircraft>?,
    val selectedAircraft: Aircraft?,
    val droneStatus: DroneStatus?,
    val loading: Boolean = true
)