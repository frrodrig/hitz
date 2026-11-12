package eus.hitz.core.domain.score

import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Level

/**
 * The score grows with the *required* steps of the course: every lesson of a lesson level, every
 * story and every review. Legendary levels are optional practice and add nothing. Points are
 * spread evenly, so each step is worth the same share of [MAX].
 */
object ScoreRules {
    /** Top of the A1 scale: the whole course. */
    const val MAX = 29

    /** Completion ids of the required steps, in course order. */
    fun steps(course: Course, units: Map<String, CourseUnit>): List<String> =
        course.sections.flatMap { it.units }.mapNotNull { units[it] }.flatMap { it.steps() }

    fun CourseUnit.steps(): List<String> = levels.flatMap { level ->
        when (level) {
            is Level.Lessons -> level.lessons.map { it.id }
            is Level.StoryLevel, is Level.Review -> listOf(level.id)
            is Level.Legendary -> emptyList()
        }
    }

    /** Exact points (with decimals) for [done] steps out of [total]. */
    fun points(done: Int, total: Int): Float = if (total == 0) 0f else MAX * done.toFloat() / total

    fun value(points: Float): Int = points.toInt().coerceIn(0, MAX)

    fun progressToNext(points: Float): Float {
        val value = value(points)
        return if (value >= MAX) 1f else (points - value).coerceIn(0f, 1f)
    }
}
