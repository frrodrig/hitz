package eus.hitz.core.domain.update

import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.time.EpochClock
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** A published version of the app: its number ("1.2.0"), its APK and its release page. */
data class AppRelease(val version: String, val apkUrl: String, val pageUrl: String)

/** The installed app. */
interface AppInfo {
    /** `versionName`, e.g. "1.1.0". */
    val versionName: String

    /** Debug builds are not published, so they never look for updates. */
    val isDebugBuild: Boolean
}

/** Where releases are published (GitHub Releases), and when the app last looked. */
interface ReleaseRepository {
    /** The latest published release, or null when it cannot be known (offline, no APK, an error). */
    suspend fun latestRelease(): AppRelease?

    suspend fun lastCheckMillis(): Long

    suspend fun setLastCheckMillis(millis: Long)
}

/** Version names are `MAJOR.MINOR.PATCH`, optionally with a leading "v" or a "-suffix". */
object Versions {
    private const val PARTS = 3

    fun parse(name: String): List<Int>? =
        name.trim().removePrefix("v").substringBefore('-')
            .split('.')
            .map { it.toIntOrNull() ?: return null }
            .takeIf { it.size == PARTS }

    /** Whether [candidate] is a later version than [installed]; false if either cannot be read. */
    fun isNewer(candidate: String, installed: String): Boolean {
        val new = parse(candidate) ?: return false
        val old = parse(installed) ?: return false
        val difference = new.zip(old).firstOrNull { (a, b) -> a != b } ?: return false
        return difference.first > difference.second
    }
}

/** What looking for a newer version found. */
sealed interface UpdateCheck {
    data class Available(val release: AppRelease) : UpdateCheck

    data object UpToDate : UpdateCheck

    /** Offline, no APK in the release, or an error: nothing can be said. */
    data object Failed : UpdateCheck
}

/**
 * Looks for a newer published version, at most once a day: never in debug builds, never when the
 * learner turned update checks off, and quietly when the network is not there (the next launch
 * tries again). Returns the release to offer, or null.
 */
class CheckForUpdateUseCase @Inject constructor(
    private val releases: ReleaseRepository,
    private val settings: SettingsRepository,
    private val app: AppInfo,
    private val clock: EpochClock,
) {
    suspend operator fun invoke(): AppRelease? {
        if (app.isDebugBuild || !settings.settings.first().updateChecksEnabled) return null
        val now = clock.now()
        val last = releases.lastCheckMillis()
        if (now in last until last + CHECK_INTERVAL_MILLIS) return null
        return (checkNow() as? UpdateCheck.Available)?.release
    }

    /** Whether this build can look at all: debug builds are not published. */
    val canCheck: Boolean get() = !app.isDebugBuild

    /**
     * Looks now, whatever the time of the last check and the switch: for when the learner asks
     * (the check button, or turning checks on). A successful look counts as today's check.
     */
    suspend fun checkNow(): UpdateCheck {
        if (!canCheck) return UpdateCheck.Failed
        val latest = releases.latestRelease() ?: return UpdateCheck.Failed
        releases.setLastCheckMillis(clock.now())
        return if (Versions.isNewer(latest.version, app.versionName)) {
            UpdateCheck.Available(latest)
        } else {
            UpdateCheck.UpToDate
        }
    }

    companion object {
        const val CHECK_INTERVAL_MILLIS: Long = 24 * 60 * 60 * 1000L
    }
}
