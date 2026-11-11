package eus.hitz.core.domain.gamification

/** What kind of session was played. */
enum class ActivityKind { Lesson, Story, Review, Legendary }

/** A finished session. [epochDay] is the learner's local day; [activeMillis] its study time. */
data class Activity(
    val sessionId: String,
    val kind: ActivityKind,
    val accuracy: Float,
    val perfect: Boolean,
    val activeMillis: Long,
    val completedAt: Long,
    val epochDay: Long,
)

/**
 * Study time of a day, and the daily goal in force that day (so changing the goal does not
 * rewrite the past).
 */
data class StudyDay(
    val epochDay: Long,
    val activeMillis: Long,
    val goalMillis: Long,
) {
    /** From 0 up (above 1 when the learner did more than the goal). */
    val progress: Float get() = if (goalMillis <= 0) 0f else activeMillis.toFloat() / goalMillis

    val goalMet: Boolean get() = activeMillis >= goalMillis
}

/** Everything the daily goal and achievements are computed from. */
data class PlayerData(
    val activities: List<Activity> = emptyList(),
    val studyDays: Map<Long, StudyDay> = emptyMap(),
)
