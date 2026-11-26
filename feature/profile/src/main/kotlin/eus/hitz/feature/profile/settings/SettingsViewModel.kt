package eus.hitz.feature.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.core.domain.update.AppRelease
import eus.hitz.core.domain.update.CheckForUpdateUseCase
import eus.hitz.core.domain.update.UpdateCheck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

/** The learner's switches, each in one place: the settings screen. */
internal enum class Setting { Speaking, Listening, SoundEffects, Reminder, SystemColors, UpdateChecks }

/** A check for a newer version that the learner asked for, as the version row shows it. */
internal sealed interface UpdateStatus {
    data object Idle : UpdateStatus

    data object Checking : UpdateStatus

    data object UpToDate : UpdateStatus

    data object Failed : UpdateStatus

    data class Available(val release: AppRelease) : UpdateStatus
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminders: ReminderScheduler,
    private val checkForUpdate: CheckForUpdateUseCase,
) : ViewModel() {
    /** Null until the settings are read. */
    val settings: StateFlow<UserSettings?> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)

    /** The last check the learner asked for. */
    internal val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    /** Debug builds are not published: there is nothing to look for. */
    val canCheckForUpdates: Boolean get() = checkForUpdate.canCheck

    /** Looks for a newer version now, whatever the switch and the daily check say. */
    fun checkForUpdates() {
        if (!canCheckForUpdates || _updateStatus.value == UpdateStatus.Checking) return
        _updateStatus.value = UpdateStatus.Checking
        viewModelScope.launch {
            _updateStatus.value = when (val found = checkForUpdate.checkNow()) {
                is UpdateCheck.Available -> UpdateStatus.Available(found.release)
                UpdateCheck.UpToDate -> UpdateStatus.UpToDate
                UpdateCheck.Failed -> UpdateStatus.Failed
            }
        }
    }

    /** Turning the reminder on: call with true only once notifications are allowed. */
    internal fun set(setting: Setting, enabled: Boolean) {
        viewModelScope.launch {
            when (setting) {
                Setting.Speaking -> settingsRepository.setSpeakingEnabled(enabled)
                Setting.Listening -> settingsRepository.setListeningEnabled(enabled)
                Setting.SoundEffects -> settingsRepository.setSoundEffectsEnabled(enabled)
                Setting.SystemColors -> settingsRepository.setSystemColors(enabled)
                Setting.UpdateChecks -> {
                    settingsRepository.setUpdateChecksEnabled(enabled)
                    // Turning checks on is also a way of asking: look now, not tomorrow.
                    if (enabled) checkForUpdates()
                }
                Setting.Reminder -> {
                    settingsRepository.setReminderEnabled(enabled)
                    if (enabled) reminders.schedule() else reminders.cancel()
                }
            }
        }
    }
}
