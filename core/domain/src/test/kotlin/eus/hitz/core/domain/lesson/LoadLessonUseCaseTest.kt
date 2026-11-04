package eus.hitz.core.domain.lesson

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.Lesson
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.LocalizedText
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFailsWith

class LoadLessonUseCaseTest {
    private val text = LocalizedText("x", "x")
    private val lesson = Lesson("u01-1.l1.s1", listOf(Exercise.Speak("u01-1.l1.s1.e01", "Kaixo", "Hola")))
    private val unit = CourseUnit(
        schemaVersion = 1,
        id = "u01-1",
        section = "s1",
        bookUnit = 1,
        title = text,
        goals = listOf(text),
        guidebook = Guidebook(emptyList()),
        levels = listOf(Level.Lessons("u01-1.l1", text, listOf(lesson)), Level.Review("u01-1.l2", text)),
    )
    private val repository = object : CourseRepository {
        override suspend fun getCourse(courseId: CourseId): Course = error("unused")

        override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = unit
    }

    @Test
    fun `finds the lesson inside its unit`() = runTest {
        val content = LoadLessonUseCase(repository)(CourseId.BASQUE_FROM_SPANISH, "u01-1", "u01-1.l1.s1")
        assertThat(content.lesson).isEqualTo(lesson)
        assertThat(content.unit.id).isEqualTo("u01-1")
    }

    @Test
    fun `fails for an unknown lesson`() = runTest {
        assertFailsWith<NoSuchElementException> {
            LoadLessonUseCase(repository)(CourseId.BASQUE_FROM_SPANISH, "u01-1", "nope")
        }
    }
}
