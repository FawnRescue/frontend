package org.fawnrescue.project

import App
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = createSupabaseClient(
            supabaseUrl = "https://irvsopidchmqfxbdpxqt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlydnNvcGlkY2htcWZ4YmRweHF0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDE3MDI4NDgsImV4cCI6MjAxNzI3ODg0OH0.oaKgHBwqw5WsYhM1_nYNJKGyidmEkIO6GaqjEWtVHI8"
        ) {
            install(Postgrest)
            install(GoTrue) {
                scheme = "app"
                host = "org.fawnrescue.project"
            }
            install(ComposeAuth)
        }
        client.handleDeeplinks(Intent("app://org.fawnrescue.project")) {
            println("Login")
        }
        if (intent.data !== null) {
            //TODO search for better method to split intent maybe Koin solves this and we don't have to parse the intent
            val data = intent.data.toString().split("=")
            val accessToken = data[1].split("&")[0]
            val refreshToken = data[5].split("&")[0]
            GlobalScope.launch {
                client.gotrue.importAuthToken(
                    accessToken,
                    refreshToken,
                    retrieveUser = true,
                    autoRefresh = true
                )
            }
        }

        setContent {
            App(client)
        }
    }
}