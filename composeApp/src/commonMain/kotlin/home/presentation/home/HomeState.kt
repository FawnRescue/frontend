package home.presentation.home

import repository.domain.NetworkFlightDate
import repository.domain.Mission

data class HomeState(
    val dates: Map<Mission, List<NetworkFlightDate>>,
    val datesLoading: Map<Mission, Boolean>,
    val loading: Boolean = true
)