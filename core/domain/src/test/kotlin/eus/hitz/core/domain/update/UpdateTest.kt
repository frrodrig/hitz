package eus.hitz.core.domain.update

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.core.domain.testing.FakeSettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateTest {
    private val release = AppRelease("1.2.0", "https://example.org/hitz-1.2.0.apk", "https://example.org/v1.2.0")

    private class FakeReleases(var latest: AppRelease?, var last: Long = 0) : ReleaseRepository {
        var fetches = 0

        override suspend fun latestRelease(): AppRelease? {
            fetches++
            return latest
        }

        override suspend fun lastCheckMillis() = last

        override suspend fun setLastCheckMillis(millis: Long) {
            last = millis
        }
    }

    private fun check(
        releases: FakeReleases,
        installed: String = "1.1.0",
        debug: Boolean = false,
        enabled: Boolean = true,
        now: Long = DAY * 10,
    ) = CheckForUpdateUseCase(
        releases = releases,
        settings = FakeSettingsRepository(UserSettings(updateChecksEnabled = enabled)),
        app = object : AppInfo {
            override val versionName = installed
            override val isDebugBuild = debug
        },
        clock = { now },
    )

    @Test
    fun versionsCompareNumberByNumber() {
        assertThat(Versions.isNewer("1.10.0", "1.9.3")).isTrue()
        assertThat(Versions.isNewer("v2.0.0", "1.99.99")).isTrue()
        assertThat(Versions.isNewer("1.1.0", "1.1.0")).isFalse()
        assertThat(Versions.isNewer("1.0.9", "1.1.0")).isFalse()
        assertThat(Versions.isNewer("1.2.0", "1.1.0-debug")).isTrue()
        assertThat(Versions.isNewer("latest", "1.1.0")).isFalse()
    }

    @Test
    fun aNewerReleaseIsOfferedAndTheCheckIsRemembered() = runTest {
        val releases = FakeReleases(release)
        assertThat(check(releases)()).isEqualTo(release)
        assertThat(releases.last).isEqualTo(DAY * 10)
    }

    @Test
    fun theInstalledVersionIsNotOffered() = runTest {
        assertThat(check(FakeReleases(release), installed = "1.2.0")()).isNull()
    }

    @Test
    fun itLooksAtMostOnceADay() = runTest {
        val releases = FakeReleases(release, last = DAY * 10 - 1_000)
        assertThat(check(releases)()).isNull()
        assertThat(releases.fetches).isEqualTo(0)
        assertThat(check(releases, now = DAY * 11)()).isEqualTo(release)
    }

    @Test
    fun offlineItTriesAgainNextTime() = runTest {
        val releases = FakeReleases(latest = null)
        assertThat(check(releases)()).isNull()
        assertThat(releases.last).isEqualTo(0)
    }

    @Test
    fun debugBuildsAndLearnersWhoTurnedItOffNeverLook() = runTest {
        val releases = FakeReleases(release)
        assertThat(check(releases, debug = true)()).isNull()
        assertThat(check(releases, enabled = false)()).isNull()
        assertThat(releases.fetches).isEqualTo(0)
    }

    private companion object {
        const val DAY = CheckForUpdateUseCase.CHECK_INTERVAL_MILLIS
    }
}
