package navigation.presentation

sealed interface NavigationEvent {
    data class OnNavItemClicked(val item: Int) : NavigationEvent
}