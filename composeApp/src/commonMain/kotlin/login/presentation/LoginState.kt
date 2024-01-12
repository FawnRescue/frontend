package login.presentation

data class LoginState(
    val errorLogin: String? = null,
    val sessionChecked: Boolean = false,
    val email: String = "",
    val password: String = "",
    val showSignUpDialog: Boolean = false,
    val showEmailDialog: Boolean = false,
    val rememberLogin: Boolean = false,
    val emailFromSignup: Boolean = false,
    val startNativeLogin:Boolean = false
)