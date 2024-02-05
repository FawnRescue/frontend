package login.presentation

sealed interface LoginEvent {
    data class OnShowEmailDialog(val show: Boolean, val fromSignUp: Boolean = false) : LoginEvent
    data class OnRememberLoginClicked(val remember: Boolean) : LoginEvent
    data class OnEmailChange(val email: String) : LoginEvent
    data class OnPasswordChange(val password: String) : LoginEvent

    object OnSignInGoogle : LoginEvent
    object OnSignInGithub : LoginEvent
    data class OnSignInEmail(val email: String, val password: String) : LoginEvent
    data class OnSignUpEmail(val email: String, val password: String) : LoginEvent

}
