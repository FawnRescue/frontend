package login.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Github
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { sessionStatus ->
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        _state.update {
                            it.copy(
                                sessionChecked = false,
                                errorLogin = null,
                                startNativeLogin = false
                            )
                        }
                        navigator.navigate(
                            NAV.HOME.path,
                            NavOptions(popUpTo = PopUpTo.First(true))
                        )
                    }

                    SessionStatus.LoadingFromStorage -> _state.update {
                        it.copy(
                            sessionChecked = false
                        )
                    }

                    SessionStatus.NetworkError -> _state.update {
                        it.copy(
                            sessionChecked = true,
                            errorLogin = "No Network Connection!"
                        )
                    }

                    SessionStatus.NotAuthenticated -> _state.update {
                        it.copy(
                            sessionChecked = true
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            //TODO add storage for credentials to make remember useful
            is LoginEvent.OnRememberLoginClicked -> _state.update { it.copy(rememberLogin = event.remember) }
            is LoginEvent.OnShowEmailDialog -> _state.update {
                _state.value.copy(
                    showEmailDialog = event.show,
                )
            }

            is LoginEvent.OnEmailChange -> _state.update { it.copy(email = event.email) }
            is LoginEvent.OnPasswordChange -> _state.update { it.copy(password = event.password) }
            LoginEvent.OnSignInGithub -> viewModelScope.launch {
                supabase.auth.signInWith(Github)
            }

            LoginEvent.OnSignInGoogle -> viewModelScope.launch {
                supabase.auth.signInWith(Google)
            }

            LoginEvent.OnNativeSignIn -> viewModelScope.launch {
                _state.update { it.copy(startNativeLogin = true) }
            }

            is LoginEvent.OnSignInEmail -> viewModelScope.launch {
                try {
                    supabase.auth.signInWith(Email) {
                        email = event.email
                        password = event.password
                    }
                } catch (e: BadRequestRestException) {
                    println(e)
                    val errorMessage = when {
                        e.message?.contains("invalid_grant (Invalid login credentials)") == true -> "Invalid login credentials. Please try again."
                        e.message?.contains("invalid_grant (Email not confirmed)") == true -> "Email not confirmed. Please check your inbox."
                        else -> "An unknown error occurred."
                    }
                    _state.update { it.copy(errorLogin = errorMessage) }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorLogin = "Unknown error occurred"
                        )
                    }
                }
            }

            is LoginEvent.OnSignUpEmail -> viewModelScope.launch {
                supabase.auth.signUpWith(Email) {
                    email = event.email
                    password = event.password
                }
                _state.update { it.copy(errorLogin = "Verification Email Sent. Please check your inbox.") }
            }
        }
    }
}