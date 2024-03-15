package planning.presentation.flightdate_editor

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.navigation.Navigator
import navigation.presentation.NAV
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mobilenativefoundation.store.store5.StoreReadResponse
import planning.presentation.flightdate_editor.FlightDateEditorEvent.Cancel
import planning.presentation.flightdate_editor.FlightDateEditorEvent.CloseDatePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.CloseTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.OpenDatePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.OpenEndTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.OpenStartTimePicker
import planning.presentation.flightdate_editor.FlightDateEditorEvent.Save
import planning.presentation.flightdate_editor.FlightDateEditorEvent.SelectAircraft
import repository.AircraftRepo
import repository.FlightDateRepo
import repository.MissionRepo
import repository.domain.InsertableFlightDate
import repository.domain.UserId

class FlightDateEditorViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    private val flightDateRepo by inject<FlightDateRepo>()
    private val missionRepo by inject<MissionRepo>()
    private val aircraftRepo by inject<AircraftRepo>()
    val supabase: SupabaseClient by inject<SupabaseClient>()

    private val _state = MutableStateFlow(
        FlightDateEditorState(
            selectedFlightDate = flightDateRepo.selectedFlightDate.value,
            isDatePickerOpen = false,
            isStartTimePickerOpen = false,
            isEndTimePickerOpen = false,
            date = flightDateRepo.selectedFlightDate.value?.start_date?.toLocalDateTime(TimeZone.currentSystemDefault())?.date,
            startTime = flightDateRepo.selectedFlightDate.value?.start_date?.toLocalDateTime(
                TimeZone.currentSystemDefault()
            )?.time,
            endTime = flightDateRepo.selectedFlightDate.value?.end_date?.toLocalDateTime(TimeZone.currentSystemDefault())?.time,
            isSaveEnabled = false,
            aircraftId = null,
        )
    )
    val state = _state.asStateFlow()

    init {
        loadAircrafts()
    }

    private fun loadAircrafts() {
        val authId = supabase.auth.currentUserOrNull()?.id ?: return
        val userId = UserId(authId)
        viewModelScope.launch {
            aircraftRepo.getAircrafts(userId).collect { response ->
                when (response) {
                    is StoreReadResponse.Data -> _state.update {
                        it.copy(
                            aircrafts = response.value,
                            loading = false
                        )
                    }

                    is StoreReadResponse.Error.Exception -> Napier.e(
                        "Aircraft loading error",
                        response.error
                    )

                    is StoreReadResponse.Error.Message -> Napier.e(response.message)
                    is StoreReadResponse.Loading -> _state.update { it.copy(loading = true) }
                    is StoreReadResponse.NoNewData -> _state.update { it.copy(loading = false) }

                    is StoreReadResponse.Error.Custom<*> -> TODO()
                    StoreReadResponse.Initial -> TODO()
                }
            }
        }
    }

    private fun isSaveEnabled(state: FlightDateEditorState, tz: TimeZone): Boolean {
        if (state.date == null || state.endTime == null || state.startTime == null || state.aircraftId == null) {
            // Not all information available
            return false
        }
        return if (state.selectedFlightDate == null) {
            // Create new flight date
            true
        } else {
            // Edit flight date
            val startDate = LocalDateTime(state.date, state.startTime)
            val endDate = LocalDateTime(state.date, state.endTime)
            startDate != state.selectedFlightDate.start_date.toLocalDateTime(tz) || endDate != state.selectedFlightDate.start_date.toLocalDateTime(
                tz
            )
        }
    }

    private fun MutableStateFlow<FlightDateEditorState>.updateSaveState() {
        this.update {
            it.copy(
                isSaveEnabled = isSaveEnabled(this.value, TimeZone.currentSystemDefault())
            )
        }
    }

    fun onEvent(event: FlightDateEditorEvent) {
        when (event) {
            Save -> {
                with(state.value) {
                    if (date == null || startTime == null || endTime == null || aircraftId == null) return

                    val selectedMission = missionRepo.selectedMission.value
                    if (selectedMission == null) {
                        navigator.navigate(NAV.PLANNING.path)
                        return
                    }
                    val insertableFlightDate = InsertableFlightDate(
                        start_date = LocalDateTime(
                            date,
                            startTime
                        ).toInstant(TimeZone.currentSystemDefault()),
                        end_date = LocalDateTime(date, endTime).toInstant(
                            TimeZone.currentSystemDefault()
                        ),
                        id = selectedFlightDate?.id,
                        mission = selectedMission.id,
                        aircraft = aircraftId.id
                    )
                    viewModelScope.launch { flightDateRepo.upsertFlightDate(insertableFlightDate) }
                        .invokeOnCompletion {
                            navigator.navigate(NAV.MISSION_EDITOR.path)
                        }
                }
            }

            Cancel -> {
                flightDateRepo.selectedFlightDate.value = null
                navigator.navigate(NAV.MISSION_EDITOR.path)
            }


            is CloseDatePicker -> {
                _state.update {
                    it.copy(
                        isDatePickerOpen = false,
                        date = event.date?.toLocalDateTime(TimeZone.currentSystemDefault())?.date
                            ?: it.date,
                    )
                }
                _state.updateSaveState()
            }

            is CloseTimePicker -> {
                _state.update {
                    it.copy(
                        isEndTimePickerOpen = false,
                        isStartTimePickerOpen = false,
                        startTime = if (it.isStartTimePickerOpen) event.time else it.startTime,
                        endTime = if (it.isEndTimePickerOpen) event.time else it.endTime,
                    )
                }
                _state.updateSaveState()
            }

            OpenDatePicker -> {
                _state.update {
                    it.copy(isDatePickerOpen = true)
                }
            }

            OpenEndTimePicker -> {
                _state.update {
                    it.copy(isEndTimePickerOpen = true)
                }
            }

            OpenStartTimePicker -> {
                _state.update {
                    it.copy(isStartTimePickerOpen = true)
                }
            }

            is SelectAircraft -> {
                _state.update {
                    it.copy(
                        aircraftId = event.id
                    )
                }
                _state.updateSaveState()
            }
        }
    }
}