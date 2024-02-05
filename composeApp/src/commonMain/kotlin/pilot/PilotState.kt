package pilot

import repository.domain.FlightDate
import repository.domain.FlightPlan
import repository.domain.Mission


data class PilotState(
    val date: FlightDate?,
    val plan: FlightPlan?,
    val mission: Mission?
)