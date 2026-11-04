package eus.hitz.core.domain.settings

import kotlinx.coroutines.flow.Flow

/** Learner preferences. */
data class UserSettings(
    /** Whether lessons include speaking exercises. */
    val speakingEnabled: Boolean = true,
    /** Whether a daily notification reminds the learner to keep the streak. */
    val reminderEnabled: Boolean = false,
)

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun setSpeakingEnabled(enabled: Boolean)

    suspend fun setReminderEnabled(enabled: Boolean)
}

/** Schedules the daily streak reminder (a background job on Android). */
interface ReminderScheduler {
    fun schedule()

    fun cancel()
}
