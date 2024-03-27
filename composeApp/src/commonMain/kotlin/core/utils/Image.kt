import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import repository.domain.Detection

@Composable
expect fun ImageFromByteArray(
    byteArray: ByteArray,
    modifier: Modifier = Modifier,
    scale: ContentScale = ContentScale.Fit,
)

@Composable
expect fun DetectionImageFromByteArray(
    byteArray: ByteArray,
    detection: Detection,
    modifier: Modifier = Modifier,
    scale: ContentScale = ContentScale.Fit
)