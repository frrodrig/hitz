package eus.hitz.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.gamification.RefreshPlayerUseCase
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** App-wide upkeep when the app comes to the foreground. */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val refreshPlayer: RefreshPlayerUseCase,
    private val settings: SettingsRepository,
    private val reminders: ReminderScheduler,
) : ViewModel() {
    /** Whether to start with the first-run introduction; null until the settings are read. */
    val startWithOnboarding: StateFlow<Boolean?> = settings.settings
        .map { !it.onboardingCompleted }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Regenerates hearts, applies streak freezes, closes the league week and keeps the reminder scheduled. */
    fun onForeground() {
        viewModelScope.launch {
            refreshPlayer()
            if (settings.settings.first().reminderEnabled) reminders.schedule()
        }
    }
}
