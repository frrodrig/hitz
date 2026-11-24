package eus.hitz.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.challenge.ChallengeRules
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.gamification.PlayerSummary
import eus.hitz.core.domain.path.ObserveLearningPathUseCase
import eus.hitz.core.domain.score.ObserveScoreUseCase
import eus.hitz.core.domain.score.Score
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    /** [stars]: stars earned in the star challenges, out of [maxStars]. */
    data class Ready(
        val player: PlayerSummary,
        val settings: UserSettings,
        val score: Score,
        val stars: Int = 0,
        val maxStars: Int = 0,
    ) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observePlayer: ObservePlayerUseCase,
    observeScore: ObserveScoreUseCase,
    observeLearningPath: ObserveLearningPathUseCase,
    private val settingsRepository: SettingsRepository,
    private val reminders: ReminderScheduler,
) : ViewModel() {
    val state: StateFlow<ProfileUiState> = combine(
        observePlayer(),
        settingsRepository.settings,
        observeScore(),
        observeLearningPath(),
    ) { player, settings, score, path ->
        val challenges = path.challenges
        ProfileUiState.Ready(
            player = player,
            settings = settings,
            score = score,
            stars = challenges.sumOf { it.stars },
            maxStars = challenges.size * ChallengeRules.MAX_STARS,
        ) as ProfileUiState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ProfileUiState.Loading)

    fun setDailyGoal(minutes: Int) {
        viewModelScope.launch { settingsRepository.setDailyGoalMinutes(minutes) }
    }

    fun setSpeakingEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSpeakingEnabled(enabled) }
    }

    fun setListeningEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setListeningEnabled(enabled) }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEffectsEnabled(enabled) }
    }

    fun setUpdateChecksEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setUpdateChecksEnabled(enabled) }
    }

    /** Call with true only once notifications are allowed. */
    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setReminderEnabled(enabled)
            if (enabled) reminders.schedule() else reminders.cancel()
        }
    }
}
