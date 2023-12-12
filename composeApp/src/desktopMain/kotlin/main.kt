import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import di.DesktopPrinter
import di.sharedModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get


// Desktop-only module to provide our di.DesktopPrinter
private val desktopModule = module {
    singleOf(::DesktopPrinter)
}

fun main() = application {
    startKoin {
        // Start Koin with both modules
        modules(sharedModule, desktopModule)
    }

    // Now the di.DesktopPrinter is ready to be retrieved
    val desktopPrinter = get<DesktopPrinter>(DesktopPrinter::class.java)
    print("Koin Test: ${desktopPrinter.print()}")
    //TODO desktop client wont get the session and redirect doesn't work
    val client = createSupabaseClient(
        supabaseUrl = "https://irvsopidchmqfxbdpxqt.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlydnNvcGlkY2htcWZ4YmRweHF0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDE3MDI4NDgsImV4cCI6MjAxNzI3ODg0OH0.oaKgHBwqw5WsYhM1_nYNJKGyidmEkIO6GaqjEWtVHI8"
    ) {
        install(Postgrest)
        install(GoTrue)
        install(ComposeAuth)
    }
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}