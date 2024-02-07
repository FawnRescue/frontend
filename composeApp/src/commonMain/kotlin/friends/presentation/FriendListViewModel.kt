package friends.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.UserRepo

class FriendListViewModel : ViewModel(), KoinComponent {
    val userRepo: UserRepo by inject<UserRepo>()
    val supabase: SupabaseClient by inject<SupabaseClient>()


    private val _state = MutableStateFlow(FriendListState(friends = emptyList()))
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    friends = userRepo.getAllUsers()
                        .filter { it.id != supabase.auth.currentUserOrNull()?.id },
                    loading = false
                )
            }
        }
    }

    fun onEvent(event: FriendListEvent) {

    }
}