package planning.presentation.flightdate_editor

import androidx.compose.material3.rememberDatePickerState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.domain.EditableFlightDate
import planning.domain.editable
import planning.repository.FlightDateRepo
import planning.repository.MissionRepo

class FlightDateEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val flightDateRepo by inject<FlightDateRepo>()
    val missionRepo by inject<MissionRepo>()



    private val _state = MutableStateFlow(FlightDateEditorState(
       selectedFlightDate = flightDateRepo.selectedFlightDate.value,
       editedFlightDate =
       flightDateRepo.selectedFlightDate.value?.editable()
                    ?: missionRepo.selectedMission.value?.let {
                        EditableFlightDate(null, it.id,
                            null,
                            null, null)
                    }
    ))
    val state = _state.asStateFlow()


    fun onEvent(event: FlightDateEditorEvent) {
        when (event) {
            FlightDateEditorEvent.Cancel -> {
                flightDateRepo.selectedFlightDate.value = null
                navigator.navigate(NAV.MISSION_EDITOR.path)
            }
            is FlightDateEditorEvent.Save -> TODO()
        }
    }
}