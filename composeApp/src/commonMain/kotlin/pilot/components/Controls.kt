package pilot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import hangar.domain.AircraftState
import hangar.domain.AircraftStatus


@Composable
fun Controls(
    status: AircraftStatus?,
    isExecutingCommand: Boolean,
    onArm: () -> Unit,
    onTakeoff: () -> Unit,
    onRTH: () -> Unit,
    onKill: () -> Unit,
    onELAND: () -> Unit,
    onContinue: () -> Unit,
    onDisarm: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(modifier = Modifier.padding(8.dp)) {
        if (status == null || status.state == AircraftState.NOT_CONNECTED) {
            Text("Aircraft not connected")
            return
        }
        Card(
            modifier = Modifier.fillMaxWidth().zIndex(2f),
            colors = CardDefaults.cardColors(containerColor = (if (isExecutingCommand) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background))
        ) {
            Text(modifier = Modifier.padding(start = 16.dp, top = 16.dp), text = "Hold to activate")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp)
            ) {
                items(
                    listOf(
                        "Arm" to onArm,
                        "Takeoff" to onTakeoff,
                        "Continue Mission" to onContinue,
                        "Disarm" to onDisarm,
                        "Return to Home" to onRTH,
                        "Emergency Land" to onELAND,
                        "Kill" to onKill,
                    )
                ) { (text, onClick) ->
                    val enabled = when (text) {
                        "Arm" -> status.state == AircraftState.IDLE
                        "Disarm" -> status.state == AircraftState.ARMED
                        "Takeoff" -> status.state == AircraftState.ARMED
                        "Return to Home", "Emergency Land", "Kill" -> status.state == AircraftState.IN_FLIGHT
                        "Continue Mission" -> status.state == AircraftState.IN_FLIGHT
                        else -> true
                    }
                    Button(modifier = if (!enabled) Modifier else Modifier.onTouchHeld(
                        onTouchSuccess = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        }), onClick = { }, enabled = enabled
                    ) {
                        Text(text)
                    }
                }
            }
        }
    }
}