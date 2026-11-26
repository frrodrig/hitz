package eus.hitz.feature.profile.settings

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.settings.ReminderScheduler
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.core.domain.update.AppInfo
import eus.hitz.core.domain.update.AppRelease
import eus.hitz.core.domain.update.CheckForUpdateUseCase
import eus.hitz.core.domain.update.ReleaseRepository
import eus.hitz.core.testing.FakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {
    private val release = AppRelease("1.5.0", "https://example.org/hitz-1.5.0.apk", "https://example.org/v1.5.0")

    private class FakeReleases(var latest: AppRelease?) : ReleaseRepository {
        var fetches = 0

        override suspend fun latestRelease(): AppRelease? {
            fetches++
            return latest
        }

        override suspend fun lastCheckMillis() = 0L

        override suspend fun setLastCheckMillis(millis: Long) = Unit
    }

    private val reminders = object : ReminderScheduler {
        override fun schedule() = Unit

        override fun cancel() = Unit
    }

    private val settings = FakeSettingsRepository(UserSettings(updateChecksEnabled = false))

    private fun viewModel(releases: ReleaseRepository, debug: Boolean = false) = SettingsViewModel(
        settingsRepository = settings,
        reminders = reminders,
        checkForUpdate = CheckForUpdateUseCase(
            releases = releases,
            settings = settings,
            app = object : AppInfo {
                override val versionName = "1.4.0"
                override val isDebugBuild = debug
            },
            clock = { 0L },
        ),
    )

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `asking looks now and says what it found`() {
        val releases = FakeReleases(release)
        val vm = viewModel(releases)
        assertThat(vm.updateStatus.value).isEqualTo(UpdateStatus.Idle)
        vm.checkForUpdates()
        assertThat(vm.updateStatus.value).isEqualTo(UpdateStatus.Available(release))
        releases.latest = null
        vm.checkForUpdates()
        assertThat(vm.updateStatus.value).isEqualTo(UpdateStatus.Failed)
    }

    @Test
    fun `turning checks on looks at once, turning them off does not`() {
        val releases = FakeReleases(AppRelease("1.4.0", "", ""))
        val vm = viewModel(releases)
        vm.set(Setting.UpdateChecks, true)
        assertThat(settings.settings.value.updateChecksEnabled).isTrue()
        assertThat(vm.updateStatus.value).isEqualTo(UpdateStatus.UpToDate)
        vm.set(Setting.UpdateChecks, false)
        assertThat(releases.fetches).isEqualTo(1)
    }

    @Test
    fun `debug builds offer no check`() {
        val releases = FakeReleases(release)
        val vm = viewModel(releases, debug = true)
        assertThat(vm.canCheckForUpdates).isFalse()
        vm.checkForUpdates()
        assertThat(vm.updateStatus.value).isEqualTo(UpdateStatus.Idle)
        assertThat(releases.fetches).isEqualTo(0)
    }
}
