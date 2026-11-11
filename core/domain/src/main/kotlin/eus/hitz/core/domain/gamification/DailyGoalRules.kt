// The numbers are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.gamification

/**
 * The daily goal: a few minutes of active practice every day, which is what spaced practice
 * needs. Only active time counts: each step of a session (an answer, a card, a story line)
 * counts the time since the previous one, up to [MAX_STEP_MILLIS], so a lesson left open on
 * the table adds at most a minute.
 */
object DailyGoalRules {
    const val MAX_STEP_MILLIS = 60_000L
    const val DEFAULT_GOAL_MINUTES = 10
    val GOAL_OPTIONS = listOf(5, 10, 15, 20)

    fun step(elapsedMillis: Long): Long = elapsedMillis.coerceIn(0, MAX_STEP_MILLIS)

    fun goalMillis(minutes: Int): Long = minutes * 60_000L

    /** Whole minutes still missing for the goal (at least 1 while it is not met). */
    fun minutesLeft(day: StudyDay): Int {
        val left = day.goalMillis - day.activeMillis
        return if (left <= 0) 0 else ((left + 59_999) / 60_000).toInt()
    }
}
