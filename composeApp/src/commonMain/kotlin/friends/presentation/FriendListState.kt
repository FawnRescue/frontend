package friends.presentation

import repository.domain.User

data class FriendListState(
    val friends: List<User> = emptyList(),
    val loading: Boolean = true
)