package eus.hitz.core.domain.practice

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.domain.score.ScoreRules.steps
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Level
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val MEDIUM_MISTAKES = 1
private const val WEAK_MISTAKES = 3

private data class SeenWord(val eu: String, val es: String, val image: String?, val clipId: String)

/** Lessons of [this] unit the learner has completed. */
internal fun CourseUnit.completedLessons(completed: Set<String>) =
    levels.filterIsInstance<Level.Lessons>().flatMap { it.lessons }.filter { it.id in completed }

/**
 * Exercises the learner has already seen, to practise: those of completed lessons, without the
 * "new word" cards, which only make sense in their lesson.
 */
internal fun CourseUnit.seenExercises(completed: Set<String>): List<Exercise> =
    completedLessons(completed).flatMap { it.exercises }.filterNot { it is Exercise.WordIntro }

internal val Exercise.isListening: Boolean get() = this is Exercise.ListenTap || this is Exercise.ListenType

/** A started unit has at least one completed required step. */
internal fun CourseUnit.isStarted(completed: Set<String>) = steps().any { it in completed }

internal fun CourseUnit.progress(completed: Set<String>): Float {
    val steps = steps()
    return if (steps.isEmpty()) 0f else steps.count { it in completed }.toFloat() / steps.size
}

/**
 * Words the learner has seen in [this] unit, in order: the key words of its introduction (shown
 * before the first lesson) and the new-word cards of its completed lessons.
 */
internal fun CourseUnit.seenWords(completed: Set<String>, stats: MistakeStats = MistakeStats()): List<ReviewWord> {
    val intros = completedLessons(completed).flatMap { it.exercises }.filterIsInstance<Exercise.WordIntro>()
    val words = keyWords.mapIndexed { index, word -> SeenWord(word.eu, word.es, word.image, "$id.kw$index") } +
        intros.map { SeenWord(it.word, it.translation, it.image, it.id) }
    val lemmas = intros.associate { it.word.lowercase() to (it.vocabulary.firstOrNull() ?: it.word.lowercase()) }
    return words.distinctBy { it.eu.lowercase() }.map { word ->
        val key = word.eu.lowercase()
        val mistakes = stats.byLemma[lemmas[key] ?: key] ?: 0
        val strength = when {
            mistakes >= WEAK_MISTAKES -> Strength.Weak
            mistakes >= MEDIUM_MISTAKES -> Strength.Medium
            else -> Strength.Strong
        }
        ReviewWord(word.eu, word.es, word.image, word.clipId, strength)
    }
}

/**
 * The practice hub: the units the learner has started, what each one holds and how much there is
 * to practise in every [PracticeMode]. It updates when a lesson is completed.
 */
class ObservePracticeHubUseCase @Inject constructor(
    private val courses: CourseRepository,
    private val progress: ProgressRepository,
) {
    operator fun invoke(courseId: CourseId = CourseId.BASQUE_FROM_SPANISH): Flow<PracticeHub> =
        progress.observeCompleted().map { completed ->
            val course = courses.getCourse(courseId)
            val started = course.sections.flatMap { section ->
                section.units.map { courses.getUnit(courseId, it) to section.id }
            }.filter { (unit, _) -> unit.isStarted(completed) }
            val stats = started.map { (unit, _) -> progress.mistakeStats(unit.id) }
                .fold(MistakeStats(), MistakeStats::plus)
            val pool = started.flatMap { (unit, _) -> unit.seenExercises(completed) }
            PracticeHub(
                units = started.map { (unit, sectionId) ->
                    LearnedUnit(
                        unitId = unit.id,
                        sectionId = sectionId,
                        title = unit.title,
                        progress = unit.progress(completed),
                        words = unit.seenWords(completed).size,
                        guides = unit.guidebook.sections.size,
                        hasStory = unit.levels.any { it is Level.StoryLevel && it.id in completed },
                    )
                },
                weakItems = stats.byLemma.count { it.value > 0 } + stats.byStructure.count { it.value > 0 },
                available = mapOf(
                    PracticeMode.Mistakes to pool.size,
                    PracticeMode.Listening to pool.count { it.isListening },
                    PracticeMode.Speaking to pool.count { it is Exercise.Speak },
                ),
            )
        }
}

/** Everything to review in one unit: words with how well they are known, notes, phrases and story. */
class GetUnitReviewUseCase @Inject constructor(
    private val courses: CourseRepository,
    private val progress: ProgressRepository,
) {
    suspend operator fun invoke(unitId: String, courseId: CourseId = CourseId.BASQUE_FROM_SPANISH): UnitReview {
        val unit = courses.getUnit(courseId, unitId)
        val completed = progress.observeCompleted().first()
        val stats = progress.mistakeStats(unitId)
        val story = unit.levels.filterIsInstance<Level.StoryLevel>().firstOrNull()
        return UnitReview(
            unitId = unit.id,
            title = unit.title,
            goals = unit.goals,
            progress = unit.progress(completed),
            words = unit.seenWords(completed, stats),
            guide = unit.guidebook.sections,
            phrases = unit.guidebook.sections.flatMap { it.examples },
            story = story?.let { ReviewStory(unit.id, it.id, it.title, completed = it.id in completed) },
        )
    }
}
