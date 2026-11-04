package eus.hitz.feature.lesson

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Guidebook
import eus.hitz.core.model.content.ImageOption
import eus.hitz.core.model.content.Language
import eus.hitz.core.model.content.Lesson
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.LocalizedText
import eus.hitz.core.model.content.Phrase
import eus.hitz.core.model.content.WordPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal object TestLesson {
    const val UNIT = "u01-1"
    const val LEVEL = "u01-1.l1"
    const val LESSON = "u01-1.l1.s1"

    val wordBank = Exercise.TranslateWordBank(
        id = "$LESSON.e01",
        prompt = Phrase(Language.Basque, "Kaixo! Ni Ane naiz."),
        answers = listOf("¡Hola! Soy Ane.", "¡Hola! Yo soy Ane."),
        distractors = listOf("eres", "adiós"),
        vocabulary = listOf("kaixo"),
        structures = listOf("izan-present"),
    )
    val typing = Exercise.TranslateTyping(
        id = "$LESSON.e02",
        prompt = Phrase(Language.Spanish, "Muchas gracias."),
        answers = listOf("Eskerrik asko."),
        vocabulary = listOf("eskerrik asko"),
    )
    val image = Exercise.ChooseImage(
        id = "$LESSON.e03",
        prompt = "buenas noches",
        options = listOf(ImageOption("egun on", "a.webp"), ImageOption("gabon", "b.webp")),
        correct = 1,
    )
    val pairs = Exercise.MatchPairs(
        id = "$LESSON.e04",
        pairs = listOf(WordPair("kaixo", "hola"), WordPair("agur", "adiós"), WordPair("barkatu", "perdón")),
        vocabulary = listOf("kaixo", "agur", "barkatu"),
    )
    val unsupported = Exercise.Speak("$LESSON.e05", "Egun on!", "¡Buenos días!")

    fun repository(vararg exercises: Exercise): CourseRepository {
        val text = LocalizedText("x", "x")
        val unit = CourseUnit(
            schemaVersion = 1,
            id = UNIT,
            section = "s1",
            bookUnit = 1,
            title = text,
            goals = listOf(text),
            guidebook = Guidebook(emptyList()),
            levels = listOf(Level.Lessons(LEVEL, text, listOf(Lesson(LESSON, exercises.toList())))),
        )
        return object : CourseRepository {
            override suspend fun getCourse(courseId: CourseId): Course = error("unused")

            override suspend fun getUnit(courseId: CourseId, unitId: String): CourseUnit = unit
        }
    }
}

internal class FakeProgressRepository : ProgressRepository {
    val completed = mutableSetOf<String>()
    val mistakes = mutableListOf<Mistake>()

    override fun observeCompleted(): Flow<Set<String>> = flowOf(completed)

    override suspend fun recordCompletion(unitId: String, completionId: String, accuracy: Float, completedAt: Long) {
        completed += completionId
    }

    override suspend fun recordMistakes(unitId: String, mistakes: List<Mistake>, recordedAt: Long) {
        this.mistakes += mistakes
    }

    override suspend fun mistakeStats(unitId: String): MistakeStats = MistakeStats()
}
