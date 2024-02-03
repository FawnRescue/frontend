package pilot

import planning.domain.FlightPlan

data class PilotState(
    val plan: FlightPlan?,
)