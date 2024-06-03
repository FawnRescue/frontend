package hangar.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.rounded.Battery0Bar
import androidx.compose.material.icons.rounded.Battery1Bar
import androidx.compose.material.icons.rounded.Battery2Bar
import androidx.compose.material.icons.rounded.Battery3Bar
import androidx.compose.material.icons.rounded.Battery4Bar
import androidx.compose.material.icons.rounded.Battery5Bar
import androidx.compose.material.icons.rounded.Battery6Bar
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.BatteryUnknown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import core.utils.RescueIcons

@Composable
fun BatteryIndicator(batteryPercentage: Float?, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (batteryPercentage != null && batteryPercentage >= 0) {
            Text("${(batteryPercentage).toInt()}%")
            Icon(
                imageVector = when {
                    batteryPercentage < 12.5 -> RescueIcons.Battery0Bar
                    batteryPercentage < 25 -> RescueIcons.Battery1Bar
                    batteryPercentage < 37.5 -> RescueIcons.Battery2Bar
                    batteryPercentage < 50 -> RescueIcons.Battery3Bar
                    batteryPercentage < 62.5 -> RescueIcons.Battery4Bar
                    batteryPercentage < 75 -> RescueIcons.Battery5Bar
                    batteryPercentage < 87.5 -> RescueIcons.Battery6Bar
                    else -> RescueIcons.BatteryFull
                },
                contentDescription = "Battery"
            )
        } else {
            Text("?%")
            Icon(RescueIcons.BatteryUnknown, contentDescription = "Battery Unknown")
        }
    }
}