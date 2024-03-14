package planning.presentation.mission_editor

import repository.domain.InsertableMission
import repository.domain.Mission
import repository.domain.NetworkFlightDate

data class MissionEditorState(
    val selectedMission: Mission?,
    val editedMission: InsertableMission,
    val dates: List<NetworkFlightDate>,
    val datesLoading: Boolean = true,
    val editable: Boolean = false,
)