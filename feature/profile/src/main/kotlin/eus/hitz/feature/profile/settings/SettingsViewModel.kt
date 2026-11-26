package eus.hitz.feature.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

/** The learner's switches, each in one place: the settings screen. */
internal enum class Setting { Speaking, Listening, SoundEffects, Reminder, SystemColors, UpdateChecks }

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminders: ReminderScheduler,
) : ViewModel() {
    /** Null until the settings are read. */
    val settings: StateFlow<UserSettings?> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    /** Turning the reminder on: call with true only once notifications are allowed. */
    internal fun set(setting: Setting, enabled: Boolean) {
        viewModelScope.launch {
            when (setting) {
                Setting.Speaking -> settingsRepository.setSpeakingEnabled(enabled)
                Setting.Listening -> settingsRepository.setListeningEnabled(enabled)
                Setting.SoundEffects -> settingsRepository.setSoundEffectsEnabled(enabled)
                Setting.SystemColors -> settingsRepository.setSystemColors(enabled)
                Setting.UpdateChecks -> settingsRepository.setUpdateChecksEnabled(enabled)
                Setting.Reminder -> {
                    settingsRepository.setReminderEnabled(enabled)
                    if (enabled) reminders.schedule() else reminders.cancel()
                }
            }
        }
    }
}
