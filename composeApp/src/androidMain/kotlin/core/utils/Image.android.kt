import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import repository.domain.Detection

@Composable
actual fun ImageFromByteArray(byteArray: ByteArray, modifier: Modifier, scale: ContentScale) {
    var imageBitmap by remember { mutableStateOf(ImageBitmap(height = 1, width = 1)) }
    println(byteArray)
    val bitmap = BitmapFactory.decodeByteArray(
        byteArray,
        0,
        byteArray.size
    )
    if (bitmap != null) {
        imageBitmap = bitmap.asImageBitmap()
        Image(
            modifier = modifier,
            bitmap = imageBitmap,
            contentDescription = "",
            contentScale = scale
        )
    } else {
        Text(text = "Couldn't load Image!")
    }
}

@Composable
actual fun DetectionImageFromByteArray(
    byteArray: ByteArray,
    detection: Detection,
    modifier: Modifier,
    scale: ContentScale
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(byteArray) {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imageBitmap = bitmap?.asImageBitmap()
    }

    Box {
        imageBitmap?.let {
            Image(
                modifier = modifier,
                bitmap = it,
                contentDescription = null,
                contentScale = scale
            )
        } ?: Text(text = "Couldn't load Image!")

        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = Color.Red,
                topLeft = Offset(detection.x.toFloat(), detection.y.toFloat()),
                size = Size(detection.width.toFloat(), detection.height.toFloat()),
                style = Stroke(width = 2f)
            )
        }

    }
}