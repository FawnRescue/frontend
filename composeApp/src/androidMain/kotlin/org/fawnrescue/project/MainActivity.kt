package org.fawnrescue.project

import App
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import di.AndroidApplication
import di.AndroidContext
import di.AndroidPrinter
import di.sharedModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent

// Desktop-only module to provide our DesktopPrinter


class MainActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        Napier.base(DebugAntilog())
        //TODO only start when not running
        stopKoin()
        val androidModule = module {
            singleOf(::AndroidPrinter)
            single { AndroidContext(get()) }
            single { AndroidApplication(application) }
        }
        startKoin {
            // Start Koin with both modules
            androidContext(this@MainActivity)
            modules(sharedModule, androidModule)
        }

        // Now the DesktopPrinter is ready to be retrieved
        val androidPrinter = KoinJavaComponent.get<AndroidPrinter>(AndroidPrinter::class.java)
        val supabase = KoinJavaComponent.get<SupabaseClient>(SupabaseClient::class.java)
        println("Koin Test: ${androidPrinter.print()}")
        super.onCreate(savedInstanceState)
        supabase.handleDeeplinks(Intent("app://org.fawnrescue.project")) {
            println("Login")
        }
        if (intent.data !== null) {
            //TODO search for better method to split intent maybe Koin solves this and we don't have to parse the intent
            val data = intent.data.toString().split("=")
            val accessToken = data[1].split("&")[0]
            val refreshToken = data[5].split("&")[0]
            GlobalScope.launch {
                supabase.auth.importAuthToken(
                    accessToken,
                    refreshToken,
                    retrieveUser = true,
                    autoRefresh = true
                )
            }
        }
        setContent {
            App()
        }
    }
}