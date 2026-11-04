package eus.hitz.core.domain.gamification

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RulesTest {
    private val hour = 60 * 60 * 1000L

    @Test
    fun `perfect sessions earn a bonus`() {
        assertThat(XpRules.xp(ActivityKind.Lesson, perfect = false)).isEqualTo(10)
        assertThat(XpRules.xp(ActivityKind.Lesson, perfect = true)).isEqualTo(15)
        assertThat(XpRules.xp(ActivityKind.Legendary, perfect = false)).isEqualTo(20)
    }

    @Test
    fun `a heart comes back every four hours`() {
        val lost = HeartRules.lose(Wallet(hearts = 5), now = 0)
        assertThat(lost.hearts).isEqualTo(4)
        assertThat(HeartRules.nextHeartAt(lost, now = hour)).isEqualTo(4 * hour)
        val twice = HeartRules.lose(lost, now = hour)
        assertThat(HeartRules.regenerate(twice, now = 4 * hour).hearts).isEqualTo(4)
        val full = HeartRules.regenerate(twice, now = 8 * hour)
        assertThat(full.hearts).isEqualTo(5)
        assertThat(HeartRules.nextHeartAt(full, now = 8 * hour)).isNull()
    }

    @Test
    fun `hearts never go below zero or above the maximum`() {
        assertThat(HeartRules.lose(Wallet(hearts = 0, heartsUpdatedAt = 0), now = 0).hearts).isEqualTo(0)
        assertThat(HeartRules.gain(Wallet(hearts = 5), now = 0).hearts).isEqualTo(5)
    }

    @Test
    fun `the streak counts practiced days up to yesterday`() {
        val days = mapOf(8L to StreakDayKind.Practiced, 9L to StreakDayKind.Practiced, 10L to StreakDayKind.Practiced)
        assertThat(StreakRules.current(days, today = 10)).isEqualTo(3)
        assertThat(StreakRules.current(days, today = 11)).isEqualTo(3)
        assertThat(StreakRules.current(days, today = 12)).isEqualTo(0)
    }

    @Test
    fun `frozen days keep the streak without adding to it`() {
        val days = mapOf(8L to StreakDayKind.Practiced, 9L to StreakDayKind.Frozen, 10L to StreakDayKind.Practiced)
        assertThat(StreakRules.current(days, today = 10)).isEqualTo(2)
        assertThat(StreakRules.longest(days + (20L to StreakDayKind.Practiced))).isEqualTo(2)
    }

    @Test
    fun `freezes cover missed days only if there are enough`() {
        val days = mapOf(8L to StreakDayKind.Practiced)
        assertThat(StreakRules.daysToFreeze(days, today = 9, freezes = 1)).isEmpty()
        assertThat(StreakRules.daysToFreeze(days, today = 10, freezes = 1)).containsExactly(9L)
        assertThat(StreakRules.daysToFreeze(days, today = 11, freezes = 1)).isEmpty()
        assertThat(StreakRules.daysToFreeze(days, today = 11, freezes = 2)).containsExactly(9L, 10L)
    }

    @Test
    fun `daily quests are stable for a day and always include an XP goal`() {
        val quests = DailyQuests.forDay(20_000)
        assertThat(quests).hasSize(3)
        assertThat(quests).isEqualTo(DailyQuests.forDay(20_000))
        assertThat(quests.first().type).isEqualTo(QuestType.EarnXp)
        assertThat(quests.map { it.type }.toSet()).hasSize(3)
    }

    @Test
    fun `quest progress counts today's sessions`() {
        val today = listOf(activity(ActivityKind.Lesson, perfect = true), activity(ActivityKind.Story, perfect = false))
        assertThat(DailyQuests.progress(Quest(QuestType.EarnXp, 50, 15), today)).isEqualTo(30)
        assertThat(DailyQuests.progress(Quest(QuestType.CompleteSessions, 2, 5), today)).isEqualTo(2)
        assertThat(DailyQuests.progress(Quest(QuestType.PerfectSessions, 2, 10), today)).isEqualTo(1)
        assertThat(DailyQuests.progress(Quest(QuestType.CompleteReview, 1, 10), today)).isEqualTo(0)
    }

    @Test
    fun `achievement tiers follow their thresholds`() {
        val progress = AchievementProgress(AchievementType.Lessons, value = 30)
        assertThat(progress.tier).isEqualTo(2)
        assertThat(progress.nextTarget).isEqualTo(50)
        assertThat(AchievementProgress(AchievementType.Lessons, value = 500).nextTarget).isNull()
    }

    @Test
    fun `league standings are stable and grow during the week`() {
        val monday = Leagues.weekStart(20_396)
        val early = Leagues.standings(monday, tier = 2, learnerXp = 0, elapsed = 0.1)
        val late = Leagues.standings(monday, tier = 2, learnerXp = 0, elapsed = 0.9)
        assertThat(early).hasSize(Leagues.GROUP_SIZE)
        assertThat(early).isEqualTo(Leagues.standings(monday, tier = 2, learnerXp = 0, elapsed = 0.1))
        assertThat(late.sumOf { it.xp }).isGreaterThan(early.sumOf { it.xp })
        assertThat(Leagues.standings(monday, 0, learnerXp = 10_000, elapsed = 1.0).first().isLearner).isTrue()
    }

    @Test
    fun `the week starts on monday`() {
        // 2026-11-04 is a Wednesday; its Monday is 2026-11-02.
        val wednesday = java.time.LocalDate.of(2026, 11, 4).toEpochDay()
        assertThat(java.time.LocalDate.ofEpochDay(Leagues.weekStart(wednesday)).dayOfMonth).isEqualTo(2)
    }

    @Test
    fun `top ranks are promoted and bottom ranks demoted`() {
        assertThat(Leagues.outcome(rank = 1, tier = 0)).isEqualTo(LeagueOutcome.Promoted)
        assertThat(Leagues.outcome(rank = 1, tier = Leagues.TIERS - 1)).isEqualTo(LeagueOutcome.Stayed)
        assertThat(Leagues.outcome(rank = Leagues.GROUP_SIZE, tier = 0)).isEqualTo(LeagueOutcome.Stayed)
        assertThat(Leagues.outcome(rank = Leagues.GROUP_SIZE, tier = 2)).isEqualTo(LeagueOutcome.Demoted)
        assertThat(Leagues.outcome(rank = 8, tier = 2)).isEqualTo(LeagueOutcome.Stayed)
    }

    private fun activity(kind: ActivityKind, perfect: Boolean) =
        Activity("s", kind, 1f, perfect, XpRules.xp(kind, perfect), completedAt = 0, epochDay = 0)
}
