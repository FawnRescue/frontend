package home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.compose.koinInject

@Composable
fun HomeScreen() {
    val supabase = koinInject<SupabaseClient>()
    val navigator = koinInject<Navigator>()
    val scope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                navigator.navigate(NAV.PROFILE.path)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Profile"
                )
            }
        }
    }) {
        Box(modifier = Modifier.padding(it)) {

            Button(onClick = {
                scope.launch {
                    supabase.auth.signOut()
                    navigator.navigate(NAV.LOGIN.path)
                }

            }) {
                Text(text = "Logout")
            }
        }
    }
}