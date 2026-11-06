package eus.hitz.core.testing

import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.audio.SoundEffect
import eus.hitz.core.domain.audio.SoundEffects
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(initial: UserSettings = UserSettings()) : SettingsRepository {
    constructor(speakingEnabled: Boolean) : this(UserSettings(speakingEnabled = speakingEnabled))

    override val settings = MutableStateFlow(initial)

    override suspend fun setSpeakingEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(speakingEnabled = enabled)
    }

    override suspend fun setReminderEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(reminderEnabled = enabled)
    }

    override suspend fun setListeningEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(listeningEnabled = enabled)
    }

    override suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(soundEffectsEnabled = enabled)
    }

    override suspend fun markUnitIntroSeen(unitId: String) {
        settings.value = settings.value.copy(seenUnitIntros = settings.value.seenUnitIntros + unitId)
    }

    override suspend fun setOnboardingCompleted() {
        settings.value = settings.value.copy(onboardingCompleted = true)
    }
}

/** Records what was played. */
class FakeAudioProvider : AudioProvider {
    val played = mutableListOf<String>()
    val playedSlow = mutableListOf<String>()
    var stopped = false

    override fun play(clipId: String, text: String, slow: Boolean) {
        played += clipId
        if (slow) playedSlow += clipId
    }

    override fun stop() {
        stopped = true
    }
}

class FakeSoundEffects : SoundEffects {
    val played = mutableListOf<SoundEffect>()

    override fun play(effect: SoundEffect) {
        played += effect
    }
}
