// The band limits are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.score

import eus.hitz.core.model.content.LocalizedText

/**
 * Where the learner stands in the course, on a scale aligned with the CEFR: 0 to [ScoreRules.MAX]
 * covers level A1, so finishing the course gives the top score.
 */
data class Score(
    val value: Int,
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
    val isTop: Boolean get() = value >= ScoreRules.MAX
}

data class ScorePoint(val epochDay: Long, val value: Int)

data class CanDo(val unitId: String, val unitTitle: LocalizedText, val goals: List<LocalizedText>)

/** Thirds of the A1 scale, each with its own description. */
enum class ScoreBand(val range: IntRange) {
    FirstSteps(0..9),
    Basics(10..19),
    Confident(20..ScoreRules.MAX),
    ;

    companion object {
        fun of(value: Int): ScoreBand = entries.first { value.coerceIn(0, ScoreRules.MAX) in it.range }
    }
}
