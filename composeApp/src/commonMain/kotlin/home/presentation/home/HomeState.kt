package home.presentation.home

import repository.domain.Mission
import repository.domain.NetworkFlightDate

data class HomeState(
    val dates: Map<Mission, List<NetworkFlightDate>>,
    val datesLoading: Map<Mission, Boolean>,
    val loading: Boolean = true,
)