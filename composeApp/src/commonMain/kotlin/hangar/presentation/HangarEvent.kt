package hangar.presentation

import hangar.domain.Aircraft

sealed interface HangarEvent {
    object AddAircraft : HangarEvent
    data class OnSelectAircraft(val aircraft: Aircraft) : HangarEvent
    object OnDismissDialog : HangarEvent
    object OnDeleteAircraft : HangarEvent
}
