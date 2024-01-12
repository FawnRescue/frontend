package planning.presentation.mission_list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import planning.presentation.components.MissionListItem

@Composable
fun MissionListScreen(onEvent: (MissionListEvent) -> Unit, state: MissionListState) {
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(onClick = { onEvent(MissionListEvent.CreateNewMission) }) {
            Icon(Icons.Rounded.Add, contentDescription = "Add Mission")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "My missions (${state.missions.size})",
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                items(state.missions) { mission ->
                    MissionListItem(
                        mission = mission,
                        modifier = Modifier.fillMaxWidth().clickable {
                            onEvent(MissionListEvent.ExistingMissionSelected(mission))
                        }.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

