package home.presentation.home

import androidx.compose.material.ExperimentalMaterialApi
import repository.domain.Mission
import repository.domain.NetworkFlightDate

data class HomeState @OptIn(ExperimentalMaterialApi::class) constructor(
    val dates: Map<Mission, List<NetworkFlightDate>>,
    val loading: Boolean = true,
    val refreshCurrentDistance: Float = 0f,
)