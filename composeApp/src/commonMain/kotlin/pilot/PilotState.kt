package pilot

import presentation.maps.LatLong
import repository.domain.Aircraft
import repository.domain.FlightDate
import repository.domain.FlightPlan
import repository.domain.Mission


data class PilotState(
    val date: FlightDate?,
    val plan: FlightPlan?,
    val mission: Mission?,
    val aircraft: Aircraft?,
    val loading: Boolean = true,
    val positions: List<LatLong> = emptyList()
)