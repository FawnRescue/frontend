package profile

import repository.domain.InsertableUser
import repository.domain.User


data class ProfileEditorState(
    val user: User?,
    val editedUser: InsertableUser?,
)