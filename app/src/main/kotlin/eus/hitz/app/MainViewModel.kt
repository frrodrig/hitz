package eus.hitz.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.update.AppRelease
import eus.hitz.core.domain.update.CheckForUpdateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** App-wide upkeep when the app comes to the foreground. */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val reminders: ReminderScheduler,
    private val checkForUpdate: CheckForUpdateUseCase,
) : ViewModel() {
    private val _update = MutableStateFlow<AppRelease?>(null)

    /** A newer version to offer, if the daily check found one. */
    val update: StateFlow<AppRelease?> = _update.asStateFlow()

    /** Whether to start with the first-run introduction; null until the settings are read. */
    val startWithOnboarding: StateFlow<Boolean?> = settings.settings
        .map { !it.onboardingCompleted }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Whether the interface takes the system's colours (Material You); on until the learner turns it off. */
    val systemColors: StateFlow<Boolean> = settings.settings
        .map { it.systemColors }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    /** Keeps the daily goal reminder scheduled (WorkManager may have dropped it after an update). */
    fun onForeground() {
        viewModelScope.launch {
            val preferences = settings.settings.first()
            if (preferences.reminderEnabled) reminders.schedule()
            // Not during the first-run introduction; the check itself runs at most once a day.
            if (preferences.onboardingCompleted && _update.value == null) _update.value = checkForUpdate()
        }
    }

    /** Download chosen or postponed: the next offer comes with tomorrow's check. */
    fun dismissUpdate() {
        _update.value = null
    }
}
