package eus.hitz.core.domain.settings

import kotlinx.coroutines.flow.Flow

/** Learner preferences. */
data class UserSettings(
    /** Whether lessons include speaking exercises. */
    val speakingEnabled: Boolean = true,
    /** Whether a daily notification reminds the learner to keep the streak. */
    val reminderEnabled: Boolean = false,
    /** Whether lessons include listening exercises (listen and tap, listen and type). */
    val listeningEnabled: Boolean = true,
    /** Whether answers play a sound effect (and a vibration). */
    val soundEffectsEnabled: Boolean = true,
    /** Units whose introduction the learner has already seen. */
    val seenUnitIntros: Set<String> = emptySet(),
    /** Whether the first-run introduction has been completed (or skipped). */
    val onboardingCompleted: Boolean = false,
)

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun setSpeakingEnabled(enabled: Boolean)

    suspend fun setReminderEnabled(enabled: Boolean)

    suspend fun setListeningEnabled(enabled: Boolean)

    suspend fun setSoundEffectsEnabled(enabled: Boolean)

    suspend fun markUnitIntroSeen(unitId: String)

    suspend fun setOnboardingCompleted()
}

/** Schedules the daily streak reminder (a background job on Android). */
interface ReminderScheduler {
    fun schedule()

    fun cancel()
}
