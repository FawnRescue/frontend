package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.domain.InsertableUser
import repository.domain.Tables
import repository.domain.User

class UserRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()

    suspend fun getAllUsers(): List<User> =
        supabase.from(Tables.USER.path).select {
        }.decodeList<User>()

    suspend fun getOwnUser(): User? {
        val user = supabase.auth.currentUserOrNull() ?: throw Exception("user should be logged in")
        return supabase.from(Tables.USER.path).select {
            filter {
                eq("id", user.id)
            }
        }.decodeSingleOrNull<User>()
    }

    suspend fun upsertOwnUser(user: InsertableUser): User {
        return supabase.from(Tables.USER.path)
            .upsert(user) { select() }.decodeSingle()
    }
}