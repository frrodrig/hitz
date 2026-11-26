// The numbers are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.recall

import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.model.content.Exercise
import kotlin.random.Random

/**
 * The cumulative review ("Recuerda"): one after each textbook unit, that is after every second app
 * unit (`u02-2`, `u03-2`…), from the second textbook unit on. It mixes what the learner saw in the
 * units *before* that textbook unit, so older units keep coming back.
 *
 * It is optional practice: it never blocks the path and adds no points to the score. Its completion
 * is stored under [levelId] only to show it as done.
 */
object RecallRules {
    const val SIZE = 15

    /** No unit fills more than this many places, so every recall is varied. */
    const val PER_UNIT = 3

    private const val SUFFIX = "recall"
    private const val EXERCISE_WEIGHT = 3

    /** Every this many days since a unit was last studied adds a point to its exercises. */
    private const val DAYS_PER_POINT = 7
    private const val MAX_AGE_POINTS = 4
    private const val DAY_MILLIS = 86_400_000L

    fun levelId(unitId: String): String = "$unitId.$SUFFIX"

    fun isRecall(levelId: String): Boolean = levelId.endsWith(".$SUFFIX")

    fun unitOf(levelId: String): String = levelId.substringBeforeLast(".$SUFFIX")

    /**
     * Whether [unitId] closes a textbook unit with something before it to recall: the second half
     * of every textbook unit but the first (`u02-2`, not `u01-2` nor `u02-1`).
     */
    fun hasRecall(unitId: String, courseUnits: List<String>): Boolean {
        if (!unitId.endsWith("-2")) return false
        val textbookUnit = unitId.substringBefore('-')
        return courseUnits.any { it.substringBefore('-') < textbookUnit }
    }

    /** The units a recall after [unitId] draws from: every unit of an earlier textbook unit. */
    fun sourceUnits(unitId: String, courseUnits: List<String>): List<String> {
        val textbookUnit = unitId.substringBefore('-')
        return courseUnits.filter { it.substringBefore('-') < textbookUnit }
    }

    /**
     * Picks the recall from the [seen] exercises of each source unit: first those the learner gets
     * wrong most and those of units studied longest ago ([lastStudied], epoch millis), at most
     * [PER_UNIT] per unit, shuffled so every try is different.
     */
    fun select(
        seen: Map<String, List<Exercise>>,
        stats: MistakeStats,
        lastStudied: Map<String, Long>,
        now: Long,
        random: Random,
    ): List<Exercise> {
        val ranked = seen.flatMap { (unitId, exercises) -> exercises.map { unitId to it } }
            .shuffled(random)
            .sortedByDescending { (unitId, exercise) ->
                stats.score(exercise) + age(lastStudied[unitId], now)
            }
        val varied = ranked.groupBy({ it.first }, { it.second })
            .flatMap { (_, exercises) -> exercises.take(PER_UNIT) }
            .let { allowed -> ranked.map { it.second }.filter { it in allowed } }
            .take(SIZE)
        // Few units seen yet: fill the rest without the per-unit limit.
        val rest = ranked.map { it.second }.filterNot { it in varied }.take(SIZE - varied.size)
        return (varied + rest).shuffled(random)
    }

    /** Mistakes on the exercise weigh more than mistakes on its words and structures elsewhere. */
    private fun MistakeStats.score(exercise: Exercise): Int =
        EXERCISE_WEIGHT * (byExercise[exercise.id] ?: 0) +
            exercise.vocabulary.sumOf { byLemma[it] ?: 0 } +
            exercise.structures.sumOf { byStructure[it] ?: 0 }

    /** A point for every [DAYS_PER_POINT] days since the unit was last studied, up to [MAX_AGE_POINTS]. */
    private fun age(lastStudied: Long?, now: Long): Int {
        val days = lastStudied?.let { (now - it) / DAY_MILLIS } ?: 0
        return (days / DAYS_PER_POINT).toInt().coerceIn(0, MAX_AGE_POINTS)
    }
}
