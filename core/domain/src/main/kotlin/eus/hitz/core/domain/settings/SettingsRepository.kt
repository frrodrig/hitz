package eus.hitz.core.domain.settings

import kotlinx.coroutines.flow.Flow

/** Learner preferences. */
data class UserSettings(
    /** Whether lessons include speaking exercises. */
    val speakingEnabled: Boolean = true,
)

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun setSpeakingEnabled(enabled: Boolean)
}
