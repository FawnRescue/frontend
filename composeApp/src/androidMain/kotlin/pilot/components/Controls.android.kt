package pilot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import hangar.domain.AircraftState
import hangar.domain.AircraftStatus
import hangar.domain.Battery
import hangar.domain.Location
import ui.FawnRescueTheme

@Preview
@Composable
fun Controls_preview() {
    FawnRescueTheme {
        Controls(
            status = AircraftStatus(
                AircraftState.IN_FLIGHT,
                Battery(0.0f, 0.0f),
                Location(0.0, 0.0),
                Location(0.0, 0.0),
                0.0f,
                0,
                0,
                0,
                0.0,
            ),
            isExecutingCommand = true,
            onArm = { /*TODO*/ },
            onTakeoff = { /*TODO*/ },
            onRTH = { /*TODO*/ },
            onKill = { /*TODO*/ },
            onELAND = { /*TODO*/ },
            onContinue = { /*TODO*/ },
            onDisarm = { /*TODO*/ })
    }
}