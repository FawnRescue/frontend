package hangar.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import friends.presentation.FriendListEvent
import hangar.presentation.components.BatteryIndicator
import io.github.jan.supabase.compose.auth.ui.email.OutlinedEmailField
import io.github.jan.supabase.compose.auth.ui.password.OutlinedPasswordField
import io.github.jan.supabase.compose.auth.ui.password.PasswordRule
import io.github.jan.supabase.compose.auth.ui.password.rememberPasswordRuleList
import login.presentation.LoginEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HangarScreen(onEvent: (HangarEvent) -> Unit, state: HangarState) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onEvent(HangarEvent.AddAircraft)
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add aircraft"
            )
        }
    }) {
        if (state.aircrafts == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = "My aircrafts (${state.aircrafts.size})",
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            items(state.aircrafts) { aircraft ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(HangarEvent.OnSelectAircraft(aircraft)) }) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Flight,
                                contentDescription = aircraft.name,
                                modifier = Modifier.size(25.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(text = aircraft.name)
                    }
                }
            }
        }
        if (state.selectedAircraft != null) {
            Dialog(onDismissRequest = { onEvent(HangarEvent.OnDismissDialog) }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.selectedAircraft.name)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (state.droneStatus != null) {
                            BatteryIndicator(state.droneStatus.battery)
                            Text("State: ${state.droneStatus.state}")
                            Text("Location: ${state.droneStatus.location}")
                        } else {
                            Text("No Data available")
                        }
                        Button(onClick = {onEvent(HangarEvent.OnDeleteAircraft)}){
                            Text("Delete aircraft")
                        }

                    }
                }
            }
        }

    }
}
