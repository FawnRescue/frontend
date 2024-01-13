package planning.presentation.flightplan_editor

import planning.domain.Mission

data class FlightPlanEditorState(
    val selectedMission: Mission?
)