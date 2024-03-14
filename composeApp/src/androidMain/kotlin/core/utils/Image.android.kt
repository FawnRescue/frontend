import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
actual fun ImageFromByteArray(byteArray: ByteArray, modifier: Modifier, scale: ContentScale) {
    var imageBitmap by remember { mutableStateOf(ImageBitmap(height = 1, width = 1)) }
    imageBitmap = BitmapFactory.decodeByteArray(
        byteArray,
        0,
        byteArray.size
    ).asImageBitmap()
    Image(
        modifier = modifier,
        bitmap = imageBitmap,
        contentDescription = "",
        contentScale = scale
    )
}