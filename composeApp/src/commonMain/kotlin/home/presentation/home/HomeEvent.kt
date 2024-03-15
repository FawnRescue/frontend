package home.presentation.home

import repository.domain.NetworkFlightDate

sealed interface HomeEvent {
    data object Logout : HomeEvent
    data object ProfileButton : HomeEvent
    data class DateSelected(val date: NetworkFlightDate) : HomeEvent
}
