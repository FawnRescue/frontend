package hangar.presentation

import repository.domain.Aircraft

sealed interface HangarEvent {
    object AddAircraft : HangarEvent
    data class OnSelectAircraft(val aircraft: Aircraft) : HangarEvent
    data class OnFOVChange(val fov: Double) : HangarEvent
    data class OnFlightHeightChange(val height: Double) : HangarEvent
    object OnDismissDialog : HangarEvent
    object OnDeleteAircraft : HangarEvent
    object OnEditAircraft : HangarEvent
    object OnSaveAircraft : HangarEvent
}
