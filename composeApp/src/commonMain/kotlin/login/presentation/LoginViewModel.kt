package login.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import friends.domain.Friend
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.authenticatedSupabaseApi
import io.github.jan.supabase.gotrue.gotrue
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
import navigation.presentation.NavigationEnum
import navigation.presentation.NavigationViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginViewModel : ViewModel(), KoinComponent {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()

    init {
        viewModelScope.launch {
            supabase.gotrue.sessionStatus.collect {
                when (it) {
                    is SessionStatus.Authenticated -> {
                        _state.update {
                            _state.value.copy(
                                sessionChecked = false,
                                errorLogin = null
                            )
                        }
                        navigator.navigate(
                            NavigationEnum.HOME.path,
                            NavOptions(popUpTo = PopUpTo.First(true))
                        )
                    }

                    SessionStatus.LoadingFromStorage -> _state.update {
                        _state.value.copy(
                            sessionChecked = false
                        )
                    }

                    SessionStatus.NetworkError -> _state.update {
                        _state.value.copy(
                            sessionChecked = true,
                            errorLogin = "No Network Connection!"
                        )
                    }

                    SessionStatus.NotAuthenticated -> _state.update {
                        _state.value.copy(
                            sessionChecked = true
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnRememberLoginClicked -> _state.update { _state.value.copy(rememberLogin = event.remember) }
            is LoginEvent.OnShowEmailDialog -> _state.update {
                _state.value.copy(
                    showEmailDialog = event.show,
                    emailFromSignup = event.fromSignUp
                )
            }

            is LoginEvent.OnShowSignUpDialog -> _state.update { _state.value.copy(showSignUpDialog = event.show) }
            is LoginEvent.OnEmailChange -> _state.update { _state.value.copy(email = event.email) }
            is LoginEvent.OnPasswordChange -> _state.update { _state.value.copy(password = event.password) }
            LoginEvent.OnLoginGithub -> viewModelScope.launch {
                supabase.gotrue.loginWith(Github)
            }

            LoginEvent.OnLoginGoogle -> viewModelScope.launch {
                supabase.gotrue.loginWith(Google)
            }

            LoginEvent.OnSignupGithub -> viewModelScope.launch {
                supabase.gotrue.signUpWith(Github)
            }

            LoginEvent.OnSignupGoogle -> viewModelScope.launch {
                supabase.gotrue.signUpWith(Google)
            }

            is LoginEvent.OnLoginEmail -> viewModelScope.launch {
                supabase.gotrue.loginWith(Email) {
                    email = event.email
                    password = event.password
                }
            }

            is LoginEvent.OnSignupEmail -> viewModelScope.launch {
                supabase.gotrue.signUpWith(Email) {
                    email = event.email
                    password = event.password
                }
            }
        }
    }
}