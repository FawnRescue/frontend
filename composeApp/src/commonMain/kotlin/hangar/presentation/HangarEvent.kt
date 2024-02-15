package hangar.presentation

import repository.domain.Aircraft

sealed interface HangarEvent {
    object AddAircraft : HangarEvent
    data class OnSelectAircraft(val aircraft: Aircraft) : HangarEvent
    object OnDismissDialog : HangarEvent
    object OnDeleteAircraft : HangarEvent
}
