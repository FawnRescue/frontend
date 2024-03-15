package repository

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.from
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import repository.domain.AircraftId
import repository.domain.Command
import repository.domain.FlightDateId
import repository.domain.InsertableCommand
import repository.domain.NetworkCommand
import repository.domain.Tables
import repository.domain.toLocal


sealed class CommandKey {
    sealed class Read : CommandKey() {
        data class ByAircraftAndDate(val flightDateId: FlightDateId, val aircraftId: AircraftId) :
            Read() //TODO use implicit UserID when selecting
    }
}

class CommandRepo : KoinComponent {
    val supabase: SupabaseClient by inject<SupabaseClient>()


    private val store = StoreBuilder.from(fetcher = Fetcher.of { key: CommandKey ->
        when (key) {
            is CommandKey.Read.ByAircraftAndDate -> loadCommands(key.aircraftId, key.flightDateId)

        }
    }).build()

    private suspend fun loadCommands(
        aircraftId: AircraftId, flightDateId: FlightDateId,
    ): List<Command> = try {
        supabase.from(Tables.COMMAND.path).select {
            filter {
                eq("aircraft", aircraftId)
                eq("flightDate", flightDateId)
            }
        }.decodeList<NetworkCommand>().map { it.toLocal() }
    } catch (e: HttpRequestException) {
        e.message?.let { Napier.e(it) }
        listOf()
    }

    suspend fun sendCommand(command: InsertableCommand): Command {
        return supabase.from(Tables.COMMAND.path).insert(command) { select() }
            .decodeSingle<NetworkCommand>().toLocal() // error handling
    }
}