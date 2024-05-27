package planning.presentation.mission_editor

import repository.domain.InsertableMission
import repository.domain.Mission
import repository.domain.NetworkFlightDate

data class MissionEditorState(
    val selectedMission: Mission?,
    val editedMission: InsertableMission? = null,
    val dates: List<NetworkFlightDate> = emptyList(),
    val datesLoading: Boolean = true,
    val editable: Boolean = false,
)