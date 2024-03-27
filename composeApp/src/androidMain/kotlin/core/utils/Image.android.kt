import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import repository.domain.Detection

@Composable
actual fun ImageFromByteArray(byteArray: ByteArray, modifier: Modifier, scale: ContentScale) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    println(byteArray)
    LaunchedEffect(byteArray) {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imageBitmap = bitmap?.asImageBitmap()

    }
    imageBitmap?.let { bitmap ->
        Image(
            modifier = modifier,
            bitmap = bitmap,
            contentDescription = null,
            contentScale = scale
        )
    } ?: Text(text = "Couldn't load Image!")
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

    BoxWithConstraints(modifier = modifier) {
        imageBitmap?.let { bitmap ->
            val imageScaleFactor = constraints.maxWidth / bitmap.width.toFloat()
            Image(
                modifier = modifier,
                bitmap = bitmap,
                contentDescription = null,
                contentScale = scale
            )
            Canvas(Modifier.matchParentSize()) {
                drawImage(
                    image = bitmap,
                    dstSize = IntSize(
                        (bitmap.width * imageScaleFactor).toInt(),
                        (bitmap.height * imageScaleFactor).toInt()
                    )
                )

                drawRect(
                    color = Color.Red,
                    topLeft = Offset(
                        detection.x.toFloat() * imageScaleFactor,
                        detection.y.toFloat() * imageScaleFactor
                    ),
                    size = Size(
                        detection.width.toFloat() * imageScaleFactor,
                        detection.height.toFloat() * imageScaleFactor
                    ),
                    style = Stroke(width = 2f)
                )
            }
        } ?: Text(text = "Couldn't load Image!")
    }
}

