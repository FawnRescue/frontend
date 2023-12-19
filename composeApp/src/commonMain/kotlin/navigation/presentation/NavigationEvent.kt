package navigation.presentation

sealed interface NavigationEvent {
    data class OnNavItemClicked(val item: NavigationEnum) : NavigationEvent
    object OnSuccessfulLogin : NavigationEvent
    data class OnRouteChange(val item: NavigationEnum) : NavigationEvent
}