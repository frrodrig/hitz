package eus.hitz.core.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.test.assertFailsWith

class CourseIdTest {
    @Test
    fun `splits learning and base language`() {
        val id = CourseId.BASQUE_FROM_SPANISH
        assertThat(id.learningLanguage).isEqualTo("eu")
        assertThat(id.fromLanguage).isEqualTo("es")
    }

    @Test
    fun `rejects invalid formats`() {
        assertFailsWith<IllegalArgumentException> { CourseId("euskera") }
        assertFailsWith<IllegalArgumentException> { CourseId("EU-ES") }
    }
}
