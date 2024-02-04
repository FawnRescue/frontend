package home.presentation.home

import repository.domain.FlightDate
import repository.domain.Mission

data class HomeState(
    val dates: Map<Mission, List<FlightDate>>,
    val datesLoading: Map<Mission, Boolean>,
    val loading: Boolean = true
)