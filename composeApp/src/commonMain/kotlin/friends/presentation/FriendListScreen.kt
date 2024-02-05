package friends.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import friends.presentation.components.FriendListItem

@Composable
fun FriendListScreen(
    state: FriendListState,
    onEvent: (FriendListEvent) -> Unit,
) {
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

        }
    }
}