package home.presentation.home

import repository.domain.FlightDate
import repository.domain.Mission

data class HomeState(
    val dates: List<Pair<Mission, List<FlightDate>>>
)