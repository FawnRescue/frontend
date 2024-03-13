package pilot

import hangar.domain.AircraftStatus
import presentation.maps.LatLong
import repository.domain.Aircraft
import repository.domain.NetworkFlightDate
import repository.domain.FlightPlan
import repository.domain.Mission
import repository.domain.UserId


data class PilotState(
    val date: NetworkFlightDate?,
    val plan: FlightPlan?,
    val mission: Mission?,
    val aircraft: Aircraft?,
    val aircraftStatus: AircraftStatus?,
    val loading: Boolean = true, // TODO: granular loading variables for mission, date, path and aircraft
    val planLoading: Boolean = true, // TODO: granular loading variables for mission, date, path and aircraft
    val ownLocation: PersonLocation? = null,
    val helperLocations: Map<UserId, PersonLocation> = emptyMap(),
    val detections: List<DetectionLocation> = emptyList()
)

data class DetectionLocation(
    val position: LatLong,
)

data class PersonLocation(
    val position: LatLong,
    val role: RescuerRole,
)

enum class RescuerRole {
    PILOT,
    RESCUER
}