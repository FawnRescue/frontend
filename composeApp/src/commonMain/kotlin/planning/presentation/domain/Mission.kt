package planning.presentation.domain

import kotlinx.serialization.Serializable

@Serializable
data class Mission(
    val id: Int,
    val created_at: Int,
    val description: String?
)
