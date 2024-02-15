package profile

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.UserRepo
import repository.domain.InsertableUser

class ProfileEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val userRepo: UserRepo by inject<UserRepo>()

    private val _state = MutableStateFlow(ProfileEditorState(null, InsertableUser(null)))
    val state = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser(){
        viewModelScope.launch {
            _state.update {
                val user = userRepo.getOwnUser()
                it.copy(
                    user = user,
                    editedUser = InsertableUser(user?.name)
                )
            }
        }
    }
    fun onEvent(event: ProfileEditorEvent) {
        when (event) {
            ProfileEditorEvent.Cancel -> {
                navigator.goBack()
            }
            is ProfileEditorEvent.NameChanged -> {
                _state.update {
                    it.copy(editedUser = InsertableUser(event.name))
                }
            }
            ProfileEditorEvent.Save -> {
                viewModelScope.launch {
                    _state.value.editedUser?.let { userRepo.upsertOwnUser(it) }
                }.invokeOnCompletion {
                    navigator.goBack()
                }
            }
        }
    }
}