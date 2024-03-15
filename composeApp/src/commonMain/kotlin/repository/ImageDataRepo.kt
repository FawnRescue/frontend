package repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder

sealed class ImageDataKey {
    sealed class Read : ImageDataKey() {
        data class ByFileName(val fileName: String) : Read()
    }
}


class ImageDataRepo : KoinComponent {
    private val supabase: SupabaseClient by inject<SupabaseClient>()

    val store = StoreBuilder.from(fetcher = Fetcher.of { key: ImageDataKey ->
        when (key) {
            is ImageDataKey.Read.ByFileName -> loadImageByFileName(key.fileName)
        }
    }).build()

    private suspend fun loadImageByFileName(fileName: String): List<ByteArray> {
        val bucket = supabase.storage.from("images")
        return try {
            listOf(bucket.downloadAuthenticated(fileName))
        } catch (e: Error) {
            emptyList()
        }
    }
}
