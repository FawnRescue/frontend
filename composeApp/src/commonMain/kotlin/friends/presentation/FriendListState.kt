package friends.presentation

import friends.domain.Friend

data class FriendListState(
    val friends: List<Friend> = emptyList(),
    val recentlyAddedFriends: List<Friend> = emptyList(),
    val selectedFriend: Friend? = null,
    val isAddFriendSheetOpen: Boolean = false,
    val isSelectedFriendSheetOpen : Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
)