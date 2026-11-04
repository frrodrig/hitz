package eus.hitz.core.domain.testing

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.Lesson
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.core.model.content.Section
import eus.hitz.core.model.content.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal object TestContent {
    private val text = LocalizedText("x", "x")

    fun speak(id: String, vocabulary: List<String> = emptyList(), structures: List<String> = emptyList()) =
        Exercise.Speak(id, "Kaixo", "Hola", vocabulary, structures)

    /** A unit with two lesson levels (the second with two lessons), a story, a review and a legendary level. */
    fun unit(id: String) = CourseUnit(
        schemaVersion = 1,
        id = id,
        section = "s1",
        bookUnit = id.substring(1, 3).toInt(),
        title = text,
        goals = listOf(text),
        guidebook = Guidebook(emptyList()),
        levels = listOf(
            Level.Lessons("$id.l1", text, listOf(Lesson("$id.l1.s1", listOf(speak("$id.l1.s1.e01", listOf("kaixo")))))),
            Level.Lessons(
                "$id.l2",
                text,
                listOf(
                    Lesson("$id.l2.s1", listOf(speak("$id.l2.s1.e01", structures = listOf("izan-present")))),
                    Lesson("$id.l2.s2", listOf(speak("$id.l2.s2.e01"))),
                ),
            ),
            Level.StoryLevel("$id.l3", text, Story("$id.l3.story", "t", emptyList(), emptyList())),
            Level.Review("$id.l4", text),
            Level.Legendary("$id.l5", text),
        ),
    )

    val units = listOf(unit("u01-1"), unit("u01-2")).associateBy { it.id }

    val course = Course(1, "eu-es", "eu", "es", text, listOf(Section("s1", text, text, listOf("u01-1", "u01-2"))))

    val courseRepository = object : CourseRepository {
        override suspend fun getCourse(courseId: CourseId): Course = course

        override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = units.getValue(unitId)
    }
}

internal class FakeProgressRepository(var stats: MistakeStats = MistakeStats()) : ProgressRepository {
    val completed = MutableStateFlow<Set<String>>(emptySet())
    val recordedMistakes = mutableListOf<Mistake>()
    val accuracies = mutableMapOf<String, Float>()

    override fun observeCompleted(): Flow<Set<String>> = completed

    override suspend fun recordCompletion(unitId: String, completionId: String, accuracy: Float, completedAt: Long) {
        completed.value = completed.value + completionId
        accuracies[completionId] = accuracy
    }

    override suspend fun recordMistakes(unitId: String, mistakes: List<Mistake>, recordedAt: Long) {
        recordedMistakes += mistakes
    }

    override suspend fun mistakeStats(unitId: String): MistakeStats = stats
}
