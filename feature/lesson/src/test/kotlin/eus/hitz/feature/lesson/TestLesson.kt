package eus.hitz.feature.lesson

import eus.hitz.core.domain.audio.AudioProvider
import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.gamification.CompleteSessionUseCase
import eus.hitz.core.domain.gamification.GrantRewardsUseCase
import eus.hitz.core.domain.lesson.Mistake
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.session.RecordSessionUseCase
import eus.hitz.core.domain.settings.SettingsRepository
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.ChatMessage
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
import eus.hitz.core.testing.FakeGamificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    val speak = Exercise.Speak("$LESSON.e05", "Egun on!", "¡Buenos días!")
    val listenTap = Exercise.ListenTap("$LESSON.e06", "Gero arte, Jon!", distractors = listOf("gabon"))
    val listenType = Exercise.ListenType("$LESSON.e07", "Eskerrik asko!", answers = listOf("Eskerrik asko"))
    val fillBlank = Exercise.FillBlank(
        id = "$LESSON.e08",
        sentence = "Ni Ane ___.",
        options = listOf("naiz", "zara"),
        correct = 0,
        translation = "Yo soy Ane.",
    )
    val meaning = Exercise.ChooseMeaning("$LESSON.e09", "Barkatu!", listOf("¡Perdón!", "¡Gracias!", "¡De nada!"), 0)
    val chat = Exercise.CompleteChat(
        id = "$LESSON.e10",
        messages = listOf(ChatMessage("Mikel", "Kaixo! Ni Mikel naiz. Eta zu?")),
        options = listOf("Ni Itziar naiz.", "Ez horregatik."),
        correct = 0,
    )
    val trueFalse = Exercise.ReadTrueFalse("$LESSON.e11", "Egun on! Ni Jon naiz.", "Jon saluda.", answer = true)
    val order = Exercise.OrderSentence("$LESSON.e12", "Zu Mikel zara?", "¿Tú eres Mikel?")

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

internal class FakeSettingsRepository(speakingEnabled: Boolean = true) : SettingsRepository {
    override val settings = MutableStateFlow(UserSettings(speakingEnabled = speakingEnabled))

    override suspend fun setSpeakingEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(speakingEnabled = enabled)
    }

    override suspend fun setReminderEnabled(enabled: Boolean) {
        settings.value = settings.value.copy(reminderEnabled = enabled)
    }
}

internal fun FakeGamificationRepository.recordSession(progress: ProgressRepository) = RecordSessionUseCase(
    progress = progress,
    clock = { FakeGamificationRepository.NOW },
    completeSession = CompleteSessionUseCase(this, clock, GrantRewardsUseCase(this, clock)),
)

internal class FakeAudioProvider : AudioProvider {
    val played = mutableListOf<String>()
    var stopped = false

    override fun play(clipId: String, text: String) {
        played += clipId
    }

    override fun stop() {
        stopped = true
    }
}
