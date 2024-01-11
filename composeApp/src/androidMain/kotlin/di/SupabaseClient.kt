package di

import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

actual fun getSupabaseClient() = createSupabaseClient(
    supabaseUrl = "https://irvsopidchmqfxbdpxqt.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlydnNvcGlkY2htcWZ4YmRweHF0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDE3MDI4NDgsImV4cCI6MjAxNzI3ODg0OH0.oaKgHBwqw5WsYhM1_nYNJKGyidmEkIO6GaqjEWtVHI8"
) {
    install(Postgrest)
    install(GoTrue) {
        scheme = "app"
        host = "org.fawnrescue.project"
    }
    install(Realtime)
    install(ComposeAuth) {
        googleNativeLogin(serverClientId = "38625036643-ifi68503ctjs83vmmbo29lbst3g24mv6.apps.googleusercontent.com")
        appleNativeLogin()
    }
}

