package login.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import org.fawnrescue.project.R

@Composable
actual fun FawnRescueLogo(modifier: Modifier) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
        contentDescription = null
    )
}
