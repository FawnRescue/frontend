package navigation.presentation

sealed interface NavigationEvent {
    data class OnNavItemClicked(val item: NAV) : NavigationEvent
    object OnSuccessfulLogin : NavigationEvent
    data class OnRouteChange(val item: NAV) : NavigationEvent
}