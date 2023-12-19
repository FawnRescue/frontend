package navigation.presentation

sealed interface NavigationEvent {
    data class OnNavItemClicked(val item: NavigationEnum) : NavigationEvent
}