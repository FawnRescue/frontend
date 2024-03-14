import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
expect fun ImageFromByteArray(byteArray: ByteArray, modifier: Modifier = Modifier, scale: ContentScale = ContentScale.Fit)

