package friends.presentation

import friends.domain.Friend

sealed interface FriendListEvent {
    object OnAddNewFriendClick : FriendListEvent
    object DismissFriend : FriendListEvent
    data class OnFirstNameChanged(val value: String) : FriendListEvent
    data class OnLastNameChanged(val value: String) : FriendListEvent
    data class OnEmailChanged(val value: String) : FriendListEvent
    data class OnPhoneNumberChanged(val value: String) : FriendListEvent
    class OnPhotoPicked(val bytes: ByteArray) : FriendListEvent
    object OnAddPhotoClicked : FriendListEvent
    object SaveFriend : FriendListEvent
    data class SelectFriend(val friend: Friend) : FriendListEvent
    data class EditFriend(val friend: Friend) : FriendListEvent
    object DeleteFriend : FriendListEvent

}
