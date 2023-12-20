package login.presentation

import friends.domain.Friend

sealed interface LoginEvent {
    data class OnShowEmailDialog(val show: Boolean, val fromSignUp: Boolean = false) : LoginEvent
    data class OnShowSignUpDialog(val show: Boolean) : LoginEvent
    data class OnRememberLoginClicked(val remember: Boolean) : LoginEvent
    data class OnEmailChange(val email: String) : LoginEvent
    data class OnPasswordChange(val password: String) : LoginEvent

    object OnLoginGoogle : LoginEvent
    object OnSignupGoogle : LoginEvent
    object OnLoginGithub : LoginEvent
    object OnSignupGithub : LoginEvent
    data class OnLoginEmail(val email: String, val password: String) : LoginEvent
    data class OnSignupEmail(val email: String, val password: String) : LoginEvent

}
