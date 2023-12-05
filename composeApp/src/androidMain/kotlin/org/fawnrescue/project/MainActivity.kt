package org.fawnrescue.project

import App
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.github.jan.supabase.postgrest.Postgrest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = createSupabaseClient(
            supabaseUrl = "https://irvsopidchmqfxbdpxqt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlydnNvcGlkY2htcWZ4YmRweHF0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDE3MDI4NDgsImV4cCI6MjAxNzI3ODg0OH0.oaKgHBwqw5WsYhM1_nYNJKGyidmEkIO6GaqjEWtVHI8"
        ) {
            install(Postgrest)
            // TODO figure out the correct gotrue config this is sketchy
            install(GoTrue) {
                scheme = "app"
                host = "org.fawnrescue.project"
            }
            install(ComposeAuth)
        }
        client.handleDeeplinks(Intent("https://org.fawnrescue.project")){
            println("Login")
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}