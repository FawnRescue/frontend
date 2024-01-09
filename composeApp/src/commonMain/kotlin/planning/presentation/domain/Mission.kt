package planning.presentation.domain

import kotlinx.serialization.Serializable

@Serializable
data class Mission(
    val id: String?,
    val created_at: String?,
    val description: String?,
    val owner: String?,
    val plan: String?
)

@Serializable
data class InsertableMission(
    val description: String?,
)
