package planning.presentation.mission_list


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import core.utils.RescueIcons
import planning.presentation.components.MissionListItem
import planning.presentation.mission_list.MissionListEvent.CreateNewMission
import planning.presentation.mission_list.MissionListEvent.ExistingMissionSelected

@Composable
fun MissionListScreen(onEvent: (MissionListEvent) -> Unit, state: MissionListState) {
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(onClick = { onEvent(CreateNewMission) }) {
            Icon(RescueIcons.Add, contentDescription = "Add Mission")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "My missions (${state.ownMissions.size + state.otherMissions.size})",
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                if (state.loading) {
                    item {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
                items(state.ownMissions) { mission ->
                    MissionListItem(
                        mission = mission,
                        modifier = Modifier.fillMaxWidth().clickable {
                            onEvent(ExistingMissionSelected(mission))
                        },
                    )
                }
                items(state.otherMissions) { mission ->
                    MissionListItem(
                        mission = mission,
                        backgroundColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxWidth().clickable {
                            onEvent(ExistingMissionSelected(mission))
                        }.border(
                            4.dp,
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(8.dp)
                        ),
                    )
                }
            }
        }
    }
}

