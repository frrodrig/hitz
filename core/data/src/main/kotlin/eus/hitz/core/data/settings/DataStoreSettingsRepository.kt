package eus.hitz.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Learner preferences, stored with Preferences DataStore. */
internal class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override val settings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            speakingEnabled = preferences[SPEAKING_ENABLED] ?: true,
            reminderEnabled = preferences[REMINDER_ENABLED] ?: false,
            listeningEnabled = preferences[LISTENING_ENABLED] ?: true,
            soundEffectsEnabled = preferences[SOUND_EFFECTS_ENABLED] ?: true,
            seenUnitIntros = preferences[SEEN_UNIT_INTROS].orEmpty(),
            onboardingCompleted = preferences[ONBOARDING_COMPLETED] ?: false,
        )
    }

    override suspend fun setSpeakingEnabled(enabled: Boolean) {
        dataStore.edit { it[SPEAKING_ENABLED] = enabled }
    }

    override suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[REMINDER_ENABLED] = enabled }
    }

    override suspend fun setListeningEnabled(enabled: Boolean) {
        dataStore.edit { it[LISTENING_ENABLED] = enabled }
    }

    override suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        dataStore.edit { it[SOUND_EFFECTS_ENABLED] = enabled }
    }

    override suspend fun markUnitIntroSeen(unitId: String) {
        dataStore.edit { it[SEEN_UNIT_INTROS] = it[SEEN_UNIT_INTROS].orEmpty() + unitId }
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { it[ONBOARDING_COMPLETED] = true }
    }

    companion object {
        const val FILE_NAME = "settings"
        private val SPEAKING_ENABLED = booleanPreferencesKey("speaking_enabled")
        private val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        private val LISTENING_ENABLED = booleanPreferencesKey("listening_enabled")
        private val SOUND_EFFECTS_ENABLED = booleanPreferencesKey("sound_effects_enabled")
        private val SEEN_UNIT_INTROS = stringSetPreferencesKey("seen_unit_intros")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
