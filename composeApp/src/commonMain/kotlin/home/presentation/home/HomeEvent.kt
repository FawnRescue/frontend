package home.presentation.home

import repository.domain.FlightDate

sealed interface HomeEvent {
    data object Logout : HomeEvent
    data object ProfileButton : HomeEvent
    data class DateSelected(val date: FlightDate) : HomeEvent
}
