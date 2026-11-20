package eus.hitz.core.data.audio

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class SyntheticIndexTest {
    @Test
    fun theIndexMapsClipIdsToFiles() {
        val index = parseSyntheticIndex("""{"u01-1.kw0": "3f2a9c0d1e4b", "u01-1.l1.s1.e16.answer": "a1b2c3d4e5f6"}""")

        assertThat(index).isEqualTo(
            mapOf("u01-1.kw0" to "3f2a9c0d1e4b", "u01-1.l1.s1.e16.answer" to "a1b2c3d4e5f6"),
        )
    }

    @Test
    fun anEmptyIndexHasNoClips() {
        assertThat(parseSyntheticIndex("{}")).isEmpty()
    }
}
