// The numbers are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.challenge

import eus.hitz.core.domain.progress.MistakeStats
import eus.hitz.core.model.content.Exercise
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * The star challenge of a unit: [SIZE] of its hardest exercises, no hints, and no second chances.
 * Every wrong answer costs a star: none earns [MAX_STARS], and one more than [MISTAKES_ALLOWED]
 * ends the challenge without stars, to be tried again.
 *
 * The best result is kept as the challenge's completion: its stored accuracy is the share of stars
 * earned ([storedAccuracy]), so repeating a challenge never loses stars. A unit's old legendary
 * level used the same level id and an accuracy close to 1, so it reads as three stars.
 */
object ChallengeRules {
    const val SIZE = 30
    const val MAX_STARS = 3
    const val MISTAKES_ALLOWED = 2

    /** Up to this many exercises come from the learner's mistakes; the rest are the hardest kinds. */
    const val FROM_MISTAKES = 10

    /** No exercise type fills more than this many places, so each challenge is varied. */
    const val PER_TYPE = 8

    private const val EXERCISE_WEIGHT = 3

    /** Stars for a finished challenge with [mistakes] wrong answers; 0 when it was failed. */
    fun stars(mistakes: Int): Int = (MAX_STARS - mistakes).coerceIn(0, MAX_STARS)

    /** What is stored as the completion's accuracy for [stars]. */
    fun storedAccuracy(stars: Int): Float = stars.toFloat() / MAX_STARS

    /** Stars from a stored best accuracy. */
    fun starsFrom(accuracy: Float): Int = (accuracy * MAX_STARS).roundToInt().coerceIn(1, MAX_STARS)

    /**
     * How demanding an exercise is: producing Basque (typing it, putting it together, hearing it)
     * is harder than recognising it. Exercises that cannot cost a star are not played at all.
     */
    fun difficulty(exercise: Exercise): Int? = when (exercise) {
        is Exercise.TranslateTyping, is Exercise.ListenType -> 4
        is Exercise.OrderSentence, is Exercise.ListenTap -> 3
        is Exercise.TranslateWordBank, is Exercise.CompleteChat, is Exercise.FillBlank -> 2
        is Exercise.ChooseMeaning, is Exercise.ChooseImage, is Exercise.ReadTrueFalse -> 1
        // Wrong pairs cost nothing, speaking is optional and new-word cards have no answer.
        is Exercise.MatchPairs, is Exercise.Speak, is Exercise.WordIntro -> null
    }

    /**
     * Picks the challenge: first the exercises the learner got wrong most (up to [FROM_MISTAKES]),
     * then the hardest kinds, at most [PER_TYPE] of each type, shuffled so every try is different.
     */
    fun select(exercises: List<Exercise>, stats: MistakeStats, random: Random): List<Exercise> {
        val playable = exercises.filter { difficulty(it) != null }.shuffled(random)
        val chosen = Selection()
        playable.map { it to stats.score(it) }
            .filter { (_, score) -> score > 0 }
            .sortedByDescending { (_, score) -> score }
            .forEach { (exercise, _) -> if (chosen.size < FROM_MISTAKES) chosen.add(exercise) }
        playable.sortedByDescending { difficulty(it) }.forEach(chosen::add)
        // A unit with few kinds of exercise still gets a full challenge.
        playable.forEach(chosen::fill)
        return chosen.exercises.shuffled(random)
    }

    /** Mistakes on the exercise weigh more than mistakes on its words and structures elsewhere. */
    private fun MistakeStats.score(exercise: Exercise): Int =
        EXERCISE_WEIGHT * (byExercise[exercise.id] ?: 0) +
            exercise.vocabulary.sumOf { byLemma[it] ?: 0 } +
            exercise.structures.sumOf { byStructure[it] ?: 0 }

    /** The exercises picked so far, up to [SIZE], with at most [PER_TYPE] of a type unless filling. */
    private class Selection {
        val exercises = mutableListOf<Exercise>()
        private val perType = mutableMapOf<String, Int>()
        val size: Int get() = exercises.size

        fun add(exercise: Exercise) {
            val type = exercise::class.simpleName.orEmpty()
            if ((perType[type] ?: 0) >= PER_TYPE) return
            if (fill(exercise)) perType[type] = (perType[type] ?: 0) + 1
        }

        fun fill(exercise: Exercise): Boolean {
            if (exercise in exercises || exercises.size >= SIZE) return false
            exercises += exercise
            return true
        }
    }
}
