package planning.presentation.mission_editor

import repository.domain.FlightDate
import repository.domain.InsertableMission
import repository.domain.Mission

data class MissionEditorState(
    val selectedMission: Mission?,
    val editedMission: InsertableMission,
    val dates: List<FlightDate>,
    val datesLoading: Boolean = true
)