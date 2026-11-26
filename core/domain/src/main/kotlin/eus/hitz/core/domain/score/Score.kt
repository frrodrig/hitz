// The band limits are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.score

import eus.hitz.core.model.content.LocalizedText

/**
 * Where the learner stands in the course, on a scale aligned with the CEFR (see [ScoreRules]):
 * finishing the course gives [max], the top of its last level.
 */
data class Score(
    val value: Int,
    /** The top of the course: what its published sections add up to (29 with A1 only). */
    val max: Int,
    /** The CEFR level being worked on: the first one not finished, or the last one. */
    val level: String,
    /** From 0 to 1: the way to the next point; 1 when the top is reached. */
    val progressToNext: Float,
    /** Points gained in the last 7 days. */
    val gainedThisWeek: Int,
    /** The score at the end of each day it changed, oldest first, ending with today. */
    val history: List<ScorePoint>,
    /** What the learner can do: the goals of every finished unit, in course order. */
    val canDo: List<CanDo>,
) {
    val band: ScoreBand get() = ScoreBand.of(value)
    val isTop: Boolean get() = value >= max
}

data class ScorePoint(val epochDay: Long, val value: Int)

data class CanDo(val unitId: String, val unitTitle: LocalizedText, val goals: List<LocalizedText>)

/** Thirds of the A1 scale and the A2 stretch, each with its own description. */
enum class ScoreBand(val range: IntRange) {
    FirstSteps(0..9),
    Basics(10..19),
    Confident(20..ScoreRules.A1_TOP),
    TowardsA2(ScoreRules.A1_TOP + 1..ScoreRules.A2_TOP),
    ;

    companion object {
        fun of(value: Int): ScoreBand = entries.first { value.coerceIn(0, ScoreRules.A2_TOP) in it.range }
    }
}
