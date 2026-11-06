package eus.hitz.feature.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.gamification.HeartRules
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.gamification.PlayerSummary
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.ObserveLearningPathUseCase
import eus.hitz.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** The counters of the path's top bar. */
data class PathStats(
    val streak: Int = 0,
    val practicedToday: Boolean = false,
    val gems: Int = 0,
    val hearts: Int = HeartRules.MAX_HEARTS,
) {
    companion object {
        fun of(player: PlayerSummary) = PathStats(player.streak, player.practicedToday, player.gems, player.hearts)
    }
}

sealed interface PathUiState {
    data object Loading : PathUiState

    data object Error : PathUiState

    /** [seenUnitIntros]: units whose introduction was already shown before their first lesson. */
    data class Ready(
        val path: LearningPath,
        val stats: PathStats = PathStats(),
        val seenUnitIntros: Set<String> = emptySet(),
    ) : PathUiState
}

@HiltViewModel
class PathViewModel @Inject constructor(
    observeLearningPath: ObserveLearningPathUseCase,
    observePlayer: ObservePlayerUseCase,
    settings: SettingsRepository,
) : ViewModel() {
    val state: StateFlow<PathUiState> = combine(
        observeLearningPath(),
        observePlayer(),
        settings.settings,
    ) { path, player, preferences ->
        PathUiState.Ready(path, PathStats.of(player), preferences.seenUnitIntros) as PathUiState
    }
        .catch { emit(PathUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PathUiState.Loading)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
