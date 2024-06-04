package pilot

import DetectionImageFromByteArray
import ImageFromByteArray
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import repository.domain.Detection

@Composable
fun DetectionDialog(
    detection: Detection,
    rgb: ByteArray?,
    thermal: ByteArray?,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (rgb == null) {
                CircularProgressIndicator()
            } else {
                ImageFromByteArray(
                    modifier = Modifier.fillMaxWidth(),
                    byteArray = rgb,
                    scale = ContentScale.FillWidth
                )
            }
            if (thermal == null) {
                CircularProgressIndicator()
            } else {
                DetectionImageFromByteArray(
                    modifier = Modifier.fillMaxWidth(),
                    byteArray = thermal,
                    detection = detection,
                    scale = ContentScale.FillWidth
                )
            }
        }
    }
}