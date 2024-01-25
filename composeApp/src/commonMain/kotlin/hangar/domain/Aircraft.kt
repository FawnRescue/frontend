package hangar.domain

import kotlinx.serialization.Serializable

@Serializable
data class Aircraft(
    val id: String,
    val owner: String,
    val name: String,
    val created_at: String,
    val description: String?,
    val key: String,
    val token: String
)
