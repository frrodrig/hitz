package eus.hitz.core.data.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreSettingsRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.repository() = DataStoreSettingsRepository(
        PreferenceDataStoreFactory.create(scope = backgroundScope) { folder.root.resolve("test.preferences_pb") },
    )

    @Test
    fun `speaking is enabled by default`() = runTest {
        assertThat(repository().settings.first().speakingEnabled).isTrue()
    }

    @Test
    fun `speaking can be turned off`() = runTest {
        val repository = repository()
        repository.setSpeakingEnabled(false)
        assertThat(repository.settings.first().speakingEnabled).isFalse()
    }

    @Test
    fun `the reminder is off until turned on`() = runTest {
        val repository = repository()
        assertThat(repository.settings.first().reminderEnabled).isFalse()
        repository.setReminderEnabled(true)
        assertThat(repository.settings.first().reminderEnabled).isTrue()
    }

    @Test
    fun `listening and sound effects are on until turned off`() = runTest {
        val repository = repository()
        val defaults = repository.settings.first()
        assertThat(defaults.listeningEnabled).isTrue()
        assertThat(defaults.soundEffectsEnabled).isTrue()
        repository.setListeningEnabled(false)
        repository.setSoundEffectsEnabled(false)
        val changed = repository.settings.first()
        assertThat(changed.listeningEnabled).isFalse()
        assertThat(changed.soundEffectsEnabled).isFalse()
    }
}
