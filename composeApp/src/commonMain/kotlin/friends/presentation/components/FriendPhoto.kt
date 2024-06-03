package friends.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.utils.RescueIcons
import repository.domain.User

@Composable
fun FriendPhoto(
    friend: User?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 25.dp,
) {
    val bitmap = null // TODO add user photos
    val photoModifier = modifier.clip(RoundedCornerShape(35))
    Box(
        modifier = photoModifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = RescueIcons.Person,
            contentDescription = friend?.name,
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}