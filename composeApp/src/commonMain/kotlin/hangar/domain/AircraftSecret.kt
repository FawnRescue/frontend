package hangar.domain

import kotlinx.serialization.Serializable

@Serializable
data class AircraftSecret(val owner: String, val token: String, val key: String)

@Serializable
data class InsertableAircraftSecret(val owner: String? = null, val token: String, val key: String)