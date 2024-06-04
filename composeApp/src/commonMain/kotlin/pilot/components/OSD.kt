package pilot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NextPlan
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import hangar.domain.AircraftStatus
import hangar.presentation.components.BatteryIndicator

enum class OSDRowType(val icon: ImageVector) {
    STATE(Icons.Default.Flight),
    LOCATION(Icons.Default.LocationOn),
    ALTITUDE(Icons.Default.Terrain),
    SATELLITES(Icons.Default.Satellite),
    MISSION_PROGRESS(Icons.Default.NextPlan)
}

@Composable
fun DisplayRow(rowType: OSDRowType, value: String) {
    Row {
        Icon(rowType.icon, contentDescription = rowType.name)
        Text(" ${rowType.name}: $value")
    }
}

@Composable
fun OSD(status: AircraftStatus?) {
    status ?: run {
        println("no state for OSD")
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).zIndex(2f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            BatteryIndicator(
                batteryPercentage = status.battery?.remainingPercent ?: 0f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            DisplayRow(OSDRowType.STATE, status.state.name)
            status.altitude?.let { DisplayRow(OSDRowType.ALTITUDE, "${it.roundToDecimals(1)}m") }
            status.numSatellites?.let { DisplayRow(OSDRowType.SATELLITES, "$it") }
            status.currentMissionItem?.let { currentMissionItem ->
                status.numMissionItems?.let { numMissionItems ->
                    DisplayRow(
                        OSDRowType.MISSION_PROGRESS,
                        "$currentMissionItem/$numMissionItems"
                    )
                }
            }
        }
    }
}