package planning.domain

import kotlinx.serialization.Serializable


@Serializable
data class Mission(
    val id: String,
    val created_at: String,
    val description: String,
    val owner: String,
    val plan: String?
)

fun Mission.insertable(): InsertableMission {
    return InsertableMission(this.description, this.id, this.plan)
}

@Serializable
data class InsertableMission(
    val description: String,
    val id: String? = null,
    val plan: String? = null,
)
