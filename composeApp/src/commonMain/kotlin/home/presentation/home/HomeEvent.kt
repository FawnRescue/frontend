package home.presentation.home

import repository.domain.NetworkFlightDate

sealed interface HomeEvent {
    data object Logout : HomeEvent
    data object Refresh : HomeEvent
    data object ProfileButton : HomeEvent
    data class DateSelected(val date: NetworkFlightDate) : HomeEvent
    data class NewRefreshDistance(val distance: Float) : HomeEvent
}
