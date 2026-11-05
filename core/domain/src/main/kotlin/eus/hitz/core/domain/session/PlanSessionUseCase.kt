package eus.hitz.core.domain.session

import eus.hitz.core.domain.content.CourseRepository
import eus.hitz.core.domain.gamification.ActivityKind
import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.domain.progress.ProgressRepository
import eus.hitz.core.model.CourseId
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Exercise
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.ReviewScope
import javax.inject.Inject
import kotlin.random.Random

/** How wrong answers are paid for in a session. */
sealed interface HeartPolicy {
    /** The learner's shared hearts (lessons). */
    data object Shared : HeartPolicy

    /** A fixed number of hearts for this session only (legendary levels). */
    data class Limited(val hearts: Int) : HeartPolicy

    /** No hearts: practice (unit reviews). */
    data object Unlimited : HeartPolicy
}

/**
 * What to play: [sessionId] is recorded as completed when the learner finishes (a lesson id, or the
 * level id of a generated review or legendary level).
 */
data class SessionPlan(
    val sessionId: String,
    val exercises: List<Exercise>,
    val hearts: HeartPolicy,
    val kind: ActivityKind,
)

/**
 * Plans a session for a level of the path.
 *
 * * Lesson levels play the requested lesson with the learner's shared hearts.
 * * Reviews (practice, no hearts) pick the unit exercises the learner struggles with most (spaced repetition by
 *   exercise, word and grammar structure), topped up with the rest.
 * * Legendary levels play every exercise of the unit, shuffled, with fewer hearts.
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
        return when (level) {
            is Level.Lessons -> {
                val lesson = level.lessons.firstOrNull { it.id == lessonId } ?: level.lessons.first()
                SessionPlan(lesson.id, lesson.exercises, HeartPolicy.Shared, ActivityKind.Lesson)
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
                    HeartPolicy.Unlimited,
                    ActivityKind.Review
                )
            }
            is Level.Legendary -> SessionPlan(
                level.id,
                unitExercises.shuffled(random),
                HeartPolicy.Limited(LEGENDARY_HEARTS),
                ActivityKind.Legendary,
            )
            is Level.StoryLevel -> throw UnsupportedOperationException("stories are played by the story player")
        }
    }

    private fun CourseUnit.lessonExercises(): List<Exercise> =
        levels.filterIsInstance<Level.Lessons>().flatMap { lessonLevel -> lessonLevel.lessons.flatMap { it.exercises } }

    companion object {
        const val LEGENDARY_HEARTS = 3

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
