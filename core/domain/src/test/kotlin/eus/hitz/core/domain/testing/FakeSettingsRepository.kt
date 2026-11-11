package eus.hitz.core.domain.testing

import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {
    override val settings = MutableStateFlow(initial)

    private fun update(transform: (UserSettings) -> UserSettings) {
        settings.value = transform(settings.value)
    }

    override suspend fun setSpeakingEnabled(enabled: Boolean) = update { it.copy(speakingEnabled = enabled) }

    override suspend fun setReminderEnabled(enabled: Boolean) = update { it.copy(reminderEnabled = enabled) }

    override suspend fun setListeningEnabled(enabled: Boolean) = update { it.copy(listeningEnabled = enabled) }

    override suspend fun setSoundEffectsEnabled(enabled: Boolean) = update { it.copy(soundEffectsEnabled = enabled) }

    override suspend fun markUnitIntroSeen(unitId: String) = update {
        it.copy(seenUnitIntros = it.seenUnitIntros + unitId)
    }

    override suspend fun setOnboardingCompleted() = update { it.copy(onboardingCompleted = true) }

    override suspend fun setDailyGoalMinutes(minutes: Int) = update { it.copy(dailyGoalMinutes = minutes) }
}
