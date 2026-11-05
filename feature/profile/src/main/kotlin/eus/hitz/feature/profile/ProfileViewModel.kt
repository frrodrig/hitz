package eus.hitz.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import eus.hitz.core.domain.gamification.PlayerSummary
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

    data class Ready(val player: PlayerSummary, val settings: UserSettings) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observePlayer: ObservePlayerUseCase,
    private val settingsRepository: SettingsRepository,
    private val reminders: ReminderScheduler,
) : ViewModel() {
    val state: StateFlow<ProfileUiState> = combine(observePlayer(), settingsRepository.settings) { player, settings ->
        ProfileUiState.Ready(player, settings) as ProfileUiState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ProfileUiState.Loading)

    fun setSpeakingEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSpeakingEnabled(enabled) }
    }

    fun setListeningEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setListeningEnabled(enabled) }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSoundEffectsEnabled(enabled) }
    }

    /** Call with true only once notifications are allowed. */
    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setReminderEnabled(enabled)
            if (enabled) reminders.schedule() else reminders.cancel()
        }
    }
}
