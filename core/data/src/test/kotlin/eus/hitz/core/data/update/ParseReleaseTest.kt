package eus.hitz.core.data.update

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.update.AppRelease
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class ParseReleaseTest {
    private fun release(draft: Boolean = false, prerelease: Boolean = false, assets: String = APK_ASSETS) = """
        {"tag_name": "v1.2.0", "html_url": "https://github.com/frrodrig/hitz/releases/tag/v1.2.0",
         "draft": $draft, "prerelease": $prerelease, "assets": $assets}
    """.trimIndent()

    @Test
    fun theApkOfTheLatestReleaseIsFound() {
        assertThat(parseRelease(release())).isEqualTo(
            AppRelease(
                version = "1.2.0",
                apkUrl = "https://github.com/frrodrig/hitz/releases/download/v1.2.0/hitz-1.2.0.apk",
                pageUrl = "https://github.com/frrodrig/hitz/releases/tag/v1.2.0",
            ),
        )
    }

    @Test
    fun draftsPreReleasesAndReleasesWithoutAnApkAreIgnored() {
        assertThat(parseRelease(release(draft = true))).isNull()
        assertThat(parseRelease(release(prerelease = true))).isNull()
        assertThat(parseRelease(release(assets = "[]"))).isNull()
    }

    private companion object {
        val APK_ASSETS = """
            [{"name": "hitz-1.2.0.aab", "browser_download_url": "https://github.com/frrodrig/hitz/releases/download/v1.2.0/hitz-1.2.0.aab"},
             {"name": "hitz-1.2.0.apk", "browser_download_url": "https://github.com/frrodrig/hitz/releases/download/v1.2.0/hitz-1.2.0.apk"}]
        """.trimIndent()
    }
}
