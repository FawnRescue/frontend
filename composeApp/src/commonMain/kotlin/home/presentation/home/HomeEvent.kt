package home.presentation.home

sealed interface HomeEvent {
    data object Logout : HomeEvent
    data object ProfileButton : HomeEvent
}
