package profile

sealed interface ProfileEditorEvent {
    data class NameChanged(val name: String) : ProfileEditorEvent
    data object Save: ProfileEditorEvent
    data object Cancel: ProfileEditorEvent
}
