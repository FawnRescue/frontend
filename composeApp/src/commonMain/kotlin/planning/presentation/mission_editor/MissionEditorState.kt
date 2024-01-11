package planning.presentation.mission_editor

import planning.domain.InsertableMission
import planning.domain.Mission

data class MissionEditorState(
    val selectedMission: Mission?,
    val editedMission: InsertableMission
)