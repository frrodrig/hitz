package eus.hitz.feature.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.gamification.LeagueView
import eus.hitz.core.domain.gamification.ObserveLeagueUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState

    data class Ready(val league: LeagueView) : LeaderboardUiState
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    observeLeague: ObserveLeagueUseCase,
) : ViewModel() {
    val state: StateFlow<LeaderboardUiState> = observeLeague()
        .map<LeagueView, LeaderboardUiState> { LeaderboardUiState.Ready(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), LeaderboardUiState.Loading)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
