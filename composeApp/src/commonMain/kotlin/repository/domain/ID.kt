package repository.domain

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class UserId(val id: String) {
    override fun toString(): String {
        return this.id
    }
}

@Serializable
@JvmInline
value class MissionId(val id: String) {
    override fun toString(): String {
        return this.id
    }
}

@Serializable
@JvmInline
value class FlightPlanId(val id: String) {
    override fun toString(): String {
        return this.id
    }
}

@Serializable
@JvmInline
value class AircraftId(val id: String) {
    override fun toString(): String {
        return this.id
    }
}
@Serializable
@JvmInline
value class FlightDateId(val id: String) {
    override fun toString(): String {
        return this.id
    }
}

@Serializable
@JvmInline
value class CommandId(val id: String) {
    override fun toString(): String {
        return this.id
    }
}