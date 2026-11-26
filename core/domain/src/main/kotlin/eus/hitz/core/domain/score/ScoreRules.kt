// The band limits are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.score

import eus.hitz.core.model.content.CefrLevel
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Level

/**
 * The score follows the CEFR: each level of the course fills its own stretch of the scale. A1 goes
 * from 0 to [A1_TOP]; A2.1, the first half of A2 (30–59), from there to [A2_1_TOP]. Inside a level
 * the points grow with its *required* steps (every lesson of a lesson level, every story and every
 * review), spread evenly; star challenges are optional practice and add nothing. Adding a level to
 * the course never lowers anybody's score.
 */
object ScoreRules {
    const val A1_TOP = 29
    const val A2_1_TOP = 44

    /** The top of each level, in course order. */
    private val TOPS = linkedMapOf(CefrLevel.A1 to A1_TOP, CefrLevel.A2_1 to A2_1_TOP)

    /** Where [level] ends on the scale. */
    fun top(level: String): Int = TOPS[level] ?: error("unknown CEFR level $level")

    /** Where [level] starts on the scale: the top of the level before it. */
    fun bottom(level: String): Int = TOPS.keys.takeWhile { it != level }.lastOrNull()?.let(TOPS::getValue) ?: 0

    /** Completion ids of the required steps of each level of the course, in course order. */
    fun stepsByLevel(course: Course, units: Map<String, CourseUnit>): Map<String, List<String>> =
        course.sections.groupBy { it.level }
            .toSortedMap(compareBy { level -> TOPS.keys.indexOf(level) })
            .mapValues { (_, sections) ->
                sections.flatMap { it.units }.mapNotNull { units[it] }.flatMap { it.steps() }
            }

    /** Completion ids of the required steps, in course order. */
    fun steps(course: Course, units: Map<String, CourseUnit>): List<String> =
        stepsByLevel(course, units).values.flatten()

    fun CourseUnit.steps(): List<String> = levels.flatMap { level ->
        when (level) {
            is Level.Lessons -> level.lessons.map { it.id }
            is Level.StoryLevel, is Level.Review -> listOf(level.id)
            is Level.Challenge -> emptyList()
        }
    }

    /** The top of the course: the top of its last level. */
    fun max(levels: Collection<String>): Int = levels.maxOfOrNull(::top) ?: A1_TOP

    /** Exact points (with decimals) for [done] of [total] steps of [level]. */
    fun points(level: String, done: Int, total: Int): Float {
        if (total == 0) return 0f
        val span = top(level) - bottom(level)
        return span * done.toFloat() / total
    }

    fun value(points: Float, max: Int): Int = points.toInt().coerceIn(0, max)

    fun progressToNext(points: Float, max: Int): Float {
        val value = value(points, max)
        return if (value >= max) 1f else (points - value).coerceIn(0f, 1f)
    }
}
