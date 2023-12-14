package friends.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import friends.domain.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendListViewModel : ViewModel() {

    private val _state = MutableStateFlow(FriendListState(friends = friends))
    val state = _state.asStateFlow()

    var newFriend: Friend? by mutableStateOf(null)
        private set

    fun onEvent(event: FriendListEvent){

    }
}

private val friends = (1..50).map {
    Friend(
        id = it.toLong(),
        firstName = "First${it}",
        lastName = "Last${it}",
        email = "test${it}@test.com",
        phoneNumber = "1231244${it}",
        photoBytes = null
    )
}