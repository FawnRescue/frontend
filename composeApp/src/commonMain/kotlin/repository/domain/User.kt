package repository.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val created_at: Instant,
    val name: String,
)

@Serializable
data class InsertableUser(
    val name: String?,
)
