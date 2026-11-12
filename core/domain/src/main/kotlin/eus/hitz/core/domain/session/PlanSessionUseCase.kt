package eus.hitz.core.domain.session

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.practice.PracticeMode
import eus.hitz.core.domain.practice.isListening
import eus.hitz.core.domain.practice.isStarted
import eus.hitz.core.domain.practice.seenExercises
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Glossary
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.ReviewScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

/**
 * What to play: [sessionId] is recorded as completed when the learner finishes (a lesson id, or the
 * level id of a generated review or legendary level).
 */
data class SessionPlan(
    val sessionId: String,
    val exercises: List<Exercise>,
    val kind: ActivityKind,
    /** Wrong answers allowed before the session ends unfinished; null for no limit. */
    val mistakesAllowed: Int? = null,
    /** Word hints for the session; none in legendary levels, which are played without help. */
    val glossary: Glossary? = null,
)

/**
 * Plans a session for a level of the path.
 *
 * * Lesson levels play the requested lesson.
 * * Reviews pick the unit exercises the learner struggles with most (spaced repetition by
 *   exercise, word and grammar structure), topped up with the rest.
 * * Legendary levels play every exercise of the unit, shuffled, allowing only
 *   [LEGENDARY_MISTAKES] wrong answers.
 */
class PlanSessionUseCase @Inject constructor(
    private val courses: CourseRepository,
    private val progress: ProgressRepository,
) {
    suspend operator fun invoke(
        unitId: String,
        levelId: String,
        lessonId: String?,
        random: Random = Random.Default,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ): SessionPlan {
        val unit = courses.getUnit(courseId, unitId)
        val level = unit.levels.firstOrNull { it.id == levelId }
            ?: throw NoSuchElementException("level $levelId not found in unit $unitId")
        val unitExercises = unit.lessonExercises()
        val glossary = courses.getGlossary(courseId)
        val plan = when (level) {
            is Level.Lessons -> {
                val lesson = level.lessons.firstOrNull { it.id == lessonId } ?: level.lessons.first()
                SessionPlan(lesson.id, lesson.exercises, ActivityKind.Lesson)
            }
            is Level.Review -> {
                val unitIds = when (level.scope) {
                    ReviewScope.Unit -> listOf(unitId)
                    ReviewScope.Section -> courses.getCourse(courseId).sections
                        .first { unitId in it.units }
                        .units
                }
                val exercises = if (unitIds == listOf(unitId)) {
                    unitExercises
                } else {
                    unitIds.flatMap { courses.getUnit(courseId, it).lessonExercises() }
                }
                val stats = unitIds.map { progress.mistakeStats(it) }.reduce(MistakeStats::plus)
                SessionPlan(
                    level.id,
                    reviewSelection(exercises, stats, random),
                    ActivityKind.Review,
                )
            }
            is Level.Legendary -> SessionPlan(
                level.id,
                unitExercises.shuffled(random),
                ActivityKind.Legendary,
                mistakesAllowed = LEGENDARY_MISTAKES,
            )
            is Level.StoryLevel -> throw UnsupportedOperationException("stories are played by the story player")
        }
        return if (plan.kind == ActivityKind.Legendary) plan else plan.copy(glossary = glossary)
    }

    /**
     * A practice session from the practice hub, made only of exercises the learner has already
     * seen (completed lessons): all started units for a [mode], or one unit when [unitId] is set.
     * Exercises are picked by mistakes, like reviews. Practice is not a step of the path: its
     * session id starts with `practice.` and is never recorded as a completion.
     *
     * @throws NoSuchElementException when there is nothing to practise yet.
     */
    suspend fun practice(
        mode: PracticeMode,
        unitId: String? = null,
        random: Random = Random.Default,
        courseId: CourseId = CourseId.BASQUE_FROM_SPANISH,
    ): SessionPlan {
        val completed = progress.observeCompleted().first()
        val units = if (unitId != null) {
            listOf(courses.getUnit(courseId, unitId))
        } else {
            courses.getCourse(courseId).sections.flatMap { it.units }.map { courses.getUnit(courseId, it) }
                .filter { it.isStarted(completed) }
        }
        val pool = units.flatMap { it.seenExercises(completed) }.filter { exercise ->
            when (mode) {
                PracticeMode.Mistakes -> true
                PracticeMode.Listening -> exercise.isListening
                PracticeMode.Speaking -> exercise is Exercise.Speak
            }
        }
        if (pool.isEmpty()) throw NoSuchElementException("nothing to practise in $mode")
        val stats = units.map { progress.mistakeStats(it.id) }.fold(MistakeStats(), MistakeStats::plus)
        val sessionId = unitId?.let { "$PRACTICE_PREFIX.unit.$it" } ?: "$PRACTICE_PREFIX.${mode.name.lowercase()}"
        return SessionPlan(
            sessionId = sessionId,
            exercises = reviewSelection(pool, stats, random),
            kind = ActivityKind.Review,
            glossary = courses.getGlossary(courseId),
        )
    }

    /** Exercises to practise: "new word" cards only make sense in their lesson. */
    private fun CourseUnit.lessonExercises(): List<Exercise> =
        levels.filterIsInstance<Level.Lessons>()
            .flatMap { lessonLevel -> lessonLevel.lessons.flatMap { it.exercises } }
            .filterNot { it is Exercise.WordIntro }

    companion object {
        const val LEGENDARY_MISTAKES = 3

        /** Session ids of practice sessions start with this; they are not steps of the path. */
        const val PRACTICE_PREFIX = "practice"

        /** Exercises in a review (unit or section). */
        const val REVIEW_SIZE = 12
        private const val EXERCISE_WEIGHT = 3

        /** Highest mistake score first; ties keep a random order so reviews vary. */
        internal fun reviewSelection(exercises: List<Exercise>, stats: MistakeStats, random: Random): List<Exercise> {
            fun score(exercise: Exercise): Int =
                EXERCISE_WEIGHT * (stats.byExercise[exercise.id] ?: 0) +
                    exercise.vocabulary.sumOf { stats.byLemma[it] ?: 0 } +
                    exercise.structures.sumOf { stats.byStructure[it] ?: 0 }
            return exercises.shuffled(random).sortedByDescending(::score).take(REVIEW_SIZE)
        }
    }
}
