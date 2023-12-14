package friends.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import friends.domain.Friend
import friends.presentation.components.FriendListItem

@Composable
fun FriendListScreen(
    state: FriendListState,
    newFriend: Friend?,
    onEvent: (FriendListEvent) -> Unit
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onEvent(FriendListEvent.OnAddNewFriendClick)
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.PersonAdd,
                contentDescription = "Add friend"
            )
        }
    }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = "My contacts (${state.friends.size})",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            items(state.friends) { friend ->
                FriendListItem(
                    friend = friend,
                    modifier = Modifier.fillMaxWidth().clickable {
                        onEvent(FriendListEvent.SelectFriend(friend))
                    }.padding(horizontal = 16.dp)
                )

            }
        }
    }
}