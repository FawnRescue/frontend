package planning.presentation.mission_editor

import repository.domain.NetworkFlightDate
import repository.domain.InsertableMission
import repository.domain.Mission

data class MissionEditorState(
    val selectedMission: Mission?,
    val editedMission: InsertableMission,
    val dates: List<NetworkFlightDate>,
    val datesLoading: Boolean = true
)