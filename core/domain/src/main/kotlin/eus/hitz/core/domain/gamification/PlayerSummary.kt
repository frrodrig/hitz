package eus.hitz.core.domain.gamification

/** Today's goal: minutes done out of the goal. [progress] can go above 1. */
data class DailyProgress(
    val activeMillis: Long,
    val goalMinutes: Int,
) {
    val goalMillis: Long get() = DailyGoalRules.goalMillis(goalMinutes)
    val progress: Float get() = if (goalMillis <= 0) 0f else activeMillis.toFloat() / goalMillis
    val percent: Int get() = (progress * PERCENT).toInt()
    val goalMet: Boolean get() = activeMillis >= goalMillis
    val minutesDone: Int get() = (activeMillis / 60_000).toInt()
    val minutesLeft: Int get() = DailyGoalRules.minutesLeft(StudyDay(0, activeMillis, goalMillis))

    private companion object {
        const val PERCENT = 100
    }
}

/** A day of the current week (Monday first) and how much of its goal was done. */
data class WeekDay(
    val epochDay: Long,
    val progress: Float,
    val isToday: Boolean,
    val isFuture: Boolean,
) {
    val goalMet: Boolean get() = progress >= 1f
}

/** What the screens show about the learner, computed from [PlayerData] at a given day. */
data class PlayerSummary(
    val today: DailyProgress,
    val week: List<WeekDay>,
    val goalDays: Int,
    val totalMinutes: Int,
    val achievements: List<AchievementProgress>,
)

private const val DAYS_PER_WEEK = 7

/** 1970-01-01 was a Thursday: day 0 is 3 days after a Monday. */
private const val EPOCH_WEEKDAY_OFFSET = 3

internal fun mondayOf(epochDay: Long): Long = epochDay - Math.floorMod(epochDay + EPOCH_WEEKDAY_OFFSET, DAYS_PER_WEEK)

/** Today always uses the current goal, even if it changed after today's last session. */
internal fun PlayerData.summary(today: Long, goalMinutes: Int): PlayerSummary {
    val goalMillis = DailyGoalRules.goalMillis(goalMinutes)
    val data = studyDays[today]?.let { copy(studyDays = studyDays + (today to it.copy(goalMillis = goalMillis))) }
        ?: this
    val monday = mondayOf(today)
    return PlayerSummary(
        today = DailyProgress(data.studyDays[today]?.activeMillis ?: 0, goalMinutes),
        week = (monday until monday + DAYS_PER_WEEK).map { day ->
            WeekDay(day, data.studyDays[day]?.progress ?: 0f, isToday = day == today, isFuture = day > today)
        },
        goalDays = data.studyDays.values.count { it.goalMet },
        totalMinutes = (data.studyDays.values.sumOf { it.activeMillis } / 60_000).toInt(),
        achievements = Achievements.progress(data),
    )
}
