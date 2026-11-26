// The band limits are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.score

import eus.hitz.core.model.content.CefrLevel
import eus.hitz.core.model.content.Course
import eus.hitz.core.model.content.CourseUnit
import eus.hitz.core.model.content.Level
import eus.hitz.core.model.content.Section

/**
 * The score follows the CEFR: each level of the course fills its own stretch of the scale. A1 goes
 * from 0 to [A1_TOP] and A2 from there to [A2_TOP]. Points grow
 * with the *required* steps (every lesson of a lesson level, every story and every review); star
 * challenges and cumulative reviews are optional practice and add nothing.
 *
 * The steps are counted in [ScoreGroup]s. A1 is one group, its points spread evenly over all its
 * steps. A later level is published section by section, so its stretch is split evenly between the
 * sections it is planned to have ([PLANNED_SECTIONS]) and each section is a group of its own:
 * publishing a new level or a new section never lowers anybody's score.
 */
object ScoreRules {
    const val A1_TOP = 29
    const val A2_TOP = 59

    /** The top of each level, in course order. */
    private val TOPS = linkedMapOf(CefrLevel.A1 to A1_TOP, CefrLevel.A2 to A2_TOP)

    /**
     * Sections each level published section by section will have; A1 was published whole. A2 has
     * four: two from the *Arian A2.1* textbook (5 and 6) and two to come with A2.2.
     */
    private val PLANNED_SECTIONS = mapOf(CefrLevel.A2 to 4)

    /** Where [level] ends on the scale. */
    fun top(level: String): Int = TOPS[level] ?: error("unknown CEFR level $level")

    /** Where [level] starts on the scale: the top of the level before it. */
    fun bottom(level: String): Int = TOPS.keys.takeWhile { it != level }.lastOrNull()?.let(TOPS::getValue) ?: 0

    /** Completion ids of the required steps of each level of the course, in course order. */
    fun stepsByLevel(course: Course, units: Map<String, CourseUnit>): Map<String, List<String>> =
        groups(course, units).groupBy { it.level }.mapValues { (_, groups) -> groups.flatMap { it.steps } }

    /** The groups the score is counted in, in course order (see [ScoreRules]). */
    fun groups(course: Course, units: Map<String, CourseUnit>): List<ScoreGroup> =
        course.sections.groupBy { it.level }
            .toSortedMap(compareBy { level -> TOPS.keys.indexOf(level) })
            .flatMap { (level, sections) ->
                val span = (top(level) - bottom(level)).toFloat()
                val planned = PLANNED_SECTIONS[level]
                fun steps(section: Section) = section.units.mapNotNull { units[it] }.flatMap { it.steps() }
                if (planned == null) {
                    listOf(ScoreGroup(level, sections.flatMap(::steps), span))
                } else {
                    sections.map { ScoreGroup(level, steps(it), span / planned) }
                }
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

    /** The top of the course: what all its groups add up to. */
    fun max(groups: List<ScoreGroup>): Int = groups.sumOf { it.span.toDouble() }.toFloat().toInt()

    /** Exact points (with decimals) for [done] of the steps of [group]. */
    fun points(group: ScoreGroup, done: Int): Float =
        if (group.steps.isEmpty()) 0f else group.span * done.toFloat() / group.steps.size

    fun value(points: Float, max: Int): Int = points.toInt().coerceIn(0, max)

    fun progressToNext(points: Float, max: Int): Float {
        val value = value(points, max)
        return if (value >= max) 1f else (points - value).coerceIn(0f, 1f)
    }
}

/** Steps of the course that share [span] points evenly (see [ScoreRules]). */
data class ScoreGroup(val level: String, val steps: List<String>, val span: Float)
