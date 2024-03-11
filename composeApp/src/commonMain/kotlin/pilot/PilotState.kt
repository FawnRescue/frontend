package pilot

import hangar.domain.AircraftStatus
import presentation.maps.LatLong
import repository.domain.Aircraft
import repository.domain.NetworkFlightDate
import repository.domain.FlightPlan
import repository.domain.Mission


data class PilotState(
    val date: NetworkFlightDate?,
    val plan: FlightPlan?,
    val mission: Mission?,
    val aircraft: Aircraft?,
    val aircraftStatus: AircraftStatus?,
    val loading: Boolean = true, // TODO: granular loading variables for mission, date, path and aircraft
    val planLoading: Boolean = true, // TODO: granular loading variables for mission, date, path and aircraft
    val positions: List<LatLong> = emptyList(),
)