import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import repository.domain.Detection

@Composable
actual fun ImageFromByteArray(
    byteArray: ByteArray,
    modifier: Modifier,
    scale: ContentScale,
) {
}

@Composable
actual fun DetectionImageFromByteArray(
    byteArray: ByteArray,
    detection: Detection,
    modifier: Modifier,
    scale: ContentScale
) {
}