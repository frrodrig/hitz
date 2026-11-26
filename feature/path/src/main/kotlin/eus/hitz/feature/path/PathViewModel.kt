package eus.hitz.feature.path

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.path.LearningPath
import eus.hitz.core.domain.path.ObserveLearningPathUseCase
import eus.hitz.core.domain.score.ObserveScoreUseCase
import eus.hitz.core.domain.score.ScoreRules
import eus.hitz.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface PathUiState {
    data object Loading : PathUiState

    data object Error : PathUiState

    /**
     * [dailyGoal]: today's progress towards the daily goal (can go over 1). [seenUnitIntros]: units
     * whose introduction was already shown before their first lesson. [score]: the learner's score,
     * out of [scoreMax]. [sectionId]: the section on show; null shows the current one.
     */
    data class Ready(
        val path: LearningPath,
        val dailyGoal: Float = 0f,
        val seenUnitIntros: Set<String> = emptySet(),
        val score: Int = 0,
        val scoreMax: Int = ScoreRules.A1_TOP,
        val sectionId: String? = null,
    ) : PathUiState
}

/**
 * The path shows one section at a time: the current one, unless the learner chose another in the
 * sections overview (which writes [SELECTED_SECTION] into this destination's saved state) or at
 * the end of a section. A section that is still locked is never shown.
 */
@HiltViewModel
class PathViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeLearningPath: ObserveLearningPathUseCase,
    observePlayer: ObservePlayerUseCase,
    observeScore: ObserveScoreUseCase,
    settings: SettingsRepository,
) : ViewModel() {
    val state: StateFlow<PathUiState> = combine(
        observeLearningPath(),
        observePlayer(),
        settings.settings,
        observeScore(),
        savedStateHandle.getStateFlow<String?>(SELECTED_SECTION, null),
    ) { path, player, preferences, score, selected ->
        val shown = selected?.takeIf { id -> path.sections.any { it.id == id && it.isUnlocked } }
        PathUiState.Ready(
            path = path,
            dailyGoal = player.today.progress,
            seenUnitIntros = preferences.seenUnitIntros,
            score = score.value,
            scoreMax = score.max,
            sectionId = shown,
        ) as PathUiState
    }
        .catch { emit(PathUiState.Error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PathUiState.Loading)

    fun selectSection(sectionId: String) {
        savedStateHandle[SELECTED_SECTION] = sectionId
    }

    companion object {
        /** Saved-state key of the section to show, written by the sections overview. */
        const val SELECTED_SECTION = "selectedSection"

        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
