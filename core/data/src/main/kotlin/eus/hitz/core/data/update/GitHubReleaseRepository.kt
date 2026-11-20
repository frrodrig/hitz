package eus.hitz.core.data.update

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import eus.hitz.core.data.content.AssetCourseRepository
import eus.hitz.core.domain.update.AppInfo
import eus.hitz.core.domain.update.AppRelease
import eus.hitz.core.domain.update.ReleaseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Named

/**
 * The latest release on GitHub, from the public API (no account, no key). This is the app's only
 * network request; any failure means "unknown", never an error for the learner.
 */
internal class GitHubReleaseRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:Named(AssetCourseRepository.IO_DISPATCHER) private val io: CoroutineDispatcher,
) : ReleaseRepository {
    override suspend fun latestRelease(): AppRelease? = withContext(io) {
        try {
            parseRelease(fetch(LATEST_RELEASE_URL))
        } catch (e: IOException) {
            Log.i(TAG, "Cannot reach GitHub Releases", e)
            null
        } catch (e: JSONException) {
            Log.w(TAG, "Unexpected answer from GitHub Releases", e)
            null
        }
    }

    override suspend fun lastCheckMillis(): Long = dataStore.data.first()[LAST_CHECK] ?: 0L

    override suspend fun setLastCheckMillis(millis: Long) {
        dataStore.edit { it[LAST_CHECK] = millis }
    }

    private fun fetch(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = TIMEOUT_MILLIS
            connection.readTimeout = TIMEOUT_MILLIS
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("User-Agent", "Hitz-Android")
            val status = connection.responseCode
            if (status != HttpURLConnection.HTTP_OK) throw IOException("HTTP $status")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val TAG = "Releases"
        const val LATEST_RELEASE_URL = "https://api.github.com/repos/frrodrig/hitz/releases/latest"
        const val TIMEOUT_MILLIS = 10_000
        val LAST_CHECK = longPreferencesKey("last_update_check")
    }
}

/** A GitHub "latest release" answer as an [AppRelease]; null for drafts, pre-releases or no APK. */
internal fun parseRelease(json: String): AppRelease? {
    val release = JSONObject(json)
    val published = !release.optBoolean("draft") && !release.optBoolean("prerelease")
    val apk = release.optJSONArray("assets")
        ?.let { assets -> (0 until assets.length()).map(assets::getJSONObject) }
        ?.firstOrNull { it.getString("name").endsWith(".apk") }
    return if (published && apk != null) {
        AppRelease(
            version = release.getString("tag_name").removePrefix("v"),
            apkUrl = apk.getString("browser_download_url"),
            pageUrl = release.getString("html_url"),
        )
    } else {
        null
    }
}

internal class AndroidAppInfo @Inject constructor(@ApplicationContext context: Context) : AppInfo {
    override val versionName: String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
    override val isDebugBuild: Boolean =
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 || context.packageName.endsWith(".debug")
}
