package eus.hitz.core.domain.gamification

/** What the screens show about the learner, computed from [PlayerData] at a given time. */
data class PlayerSummary(
    val streak: Int,
    val practicedToday: Boolean,
    val longestStreak: Int,
    val totalXp: Int,
    val weeklyXp: Int,
    val gems: Int,
    val hearts: Int,
    val nextHeartAt: Long?,
    val streakFreezes: Int,
    val league: LeagueState,
    val quests: List<QuestProgress>,
    val achievements: List<AchievementProgress>,
)

internal fun PlayerData.weeklyXp(weekStart: Long): Int =
    activities.filter { it.epochDay >= weekStart && it.epochDay < weekStart + Leagues.DAYS_PER_WEEK }.sumOf { it.xp }

internal fun PlayerData.summary(now: Long, today: Long): PlayerSummary {
    val todayActivities = activities.filter { it.epochDay == today }
    val current = HeartRules.regenerate(wallet, now)
    val week = if (league.weekStart == LeagueState.NOT_JOINED) Leagues.weekStart(today) else league.weekStart
    return PlayerSummary(
        streak = StreakRules.current(streakDays, today),
        practicedToday = streakDays[today] == StreakDayKind.Practiced,
        longestStreak = StreakRules.longest(streakDays),
        totalXp = activities.sumOf { it.xp },
        weeklyXp = weeklyXp(week),
        gems = current.gems,
        hearts = current.hearts,
        nextHeartAt = HeartRules.nextHeartAt(current, now),
        streakFreezes = current.streakFreezes,
        league = league,
        quests = DailyQuests.forDay(today).map { quest ->
            QuestProgress(
                quest = quest,
                progress = DailyQuests.progress(quest, todayActivities),
                claimed = DailyQuests.rewardId(today, quest) in claimed,
            )
        },
        achievements = Achievements.progress(this),
    )
}
