package planning.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.decodeIfNotEmptyOrDefault
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import moe.tlaster.precompose.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import planning.presentation.domain.InsertableMission
import planning.presentation.domain.Mission

class MissionListViewModel : ViewModel(), KoinComponent {
    private val navigator: Navigator by inject<Navigator>()
    val supabase: SupabaseClient by inject<SupabaseClient>()
    private val _state = MutableStateFlow(MissionListState(missions = emptyList()))
    val state = _state.asStateFlow()

    init {
        loadMissions()
        val channel = supabase.realtime.createChannel("")

        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "mission"
        }
        viewModelScope.launch {
            supabase.realtime.connect()
            channel.join()
        }
        viewModelScope.launch {
            changeFlow.collect {
                when (it) {
                    is PostgresAction.Delete -> println("Deleted: ${it.oldRecord}")
                    is PostgresAction.Insert -> _state.value = _state.value.copy(
                        _state.value.missions.plus(
                            Json.decodeFromJsonElement<Mission>(it.record)
                        )
                    )
                    is PostgresAction.Select -> println("Selected: ${it.record}")
                    is PostgresAction.Update -> println("Updated: ${it.oldRecord} with ${it.record}")
                }
            }
        }
    }

    private fun loadMissions() {
        viewModelScope.launch {
            _state.value = MissionListState(
                missions = supabase.postgrest.from("mission").select()
                    .also { println("LOADING MISSIONS") }.decodeList<Mission>()
            )

        }
    }

    private fun createMission() {
        viewModelScope.launch {
            supabase.postgrest.from("mission")
                .insert(InsertableMission("Neue Mission"))
        }
    }

    fun onEvent(event: MissionListEvent) {
        when (event) {
            MissionListEvent.CreateNewMission -> createMission()
            is MissionListEvent.ExistingMissionSelected -> TODO()
        }
    }
}