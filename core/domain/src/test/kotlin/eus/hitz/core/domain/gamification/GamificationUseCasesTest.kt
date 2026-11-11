package eus.hitz.core.domain.gamification

import com.google.common.truth.Truth.assertThat
import eus.hitz.core.domain.settings.UserSettings
import eus.hitz.core.domain.testing.FakeGamificationRepository
import eus.hitz.core.domain.testing.FakeSettingsRepository
import eus.hitz.core.domain.testing.TestClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GamificationUseCasesTest {
    private val repository = FakeGamificationRepository()
    private val settings = FakeSettingsRepository(UserSettings(dailyGoalMinutes = 10))
    private val clock = TestClock()
    private val studyTime = RecordStudyTimeUseCase(repository, settings, clock.gameClock)
    private val complete = CompleteSessionUseCase(repository, clock.gameClock, studyTime)
    private val observe = ObservePlayerUseCase(repository, settings, clock.gameClock)

    @Test
    fun `sessions add up to the daily goal and the one that completes it is marked`() = runTest {
        val first = complete("a", ActivityKind.Lesson, 1f, true, activeMillis = 6 * 60_000)
        assertThat(first.after.percent).isEqualTo(60)
        assertThat(first.goalReached).isFalse()

        val second = complete("b", ActivityKind.Lesson, 1f, false, activeMillis = 5 * 60_000)
        assertThat(second.before.percent).isEqualTo(60)
        assertThat(second.after.percent).isEqualTo(110)
        assertThat(second.goalReached).isTrue()

        val third = complete("c", ActivityKind.Story, 1f, false, activeMillis = 60_000)
        assertThat(third.goalReached).isFalse()
    }

    @Test
    fun `a session reports the achievement tiers it reached`() = runTest {
        val reward = complete("a", ActivityKind.Story, 1f, true, activeMillis = 60_000)
        assertThat(reward.achievements).containsExactly(
            AchievementTier(AchievementType.Perfect, 1),
            AchievementTier(AchievementType.Stories, 1),
        )
    }

    @Test
    fun `the summary shows today and the current week`() = runTest {
        studyTime(10 * 60_000)
        clock.advanceDays(1)
        studyTime(3 * 60_000)
        val summary = observe().first()
        assertThat(summary.today.percent).isEqualTo(30)
        assertThat(summary.today.minutesLeft).isEqualTo(7)
        assertThat(summary.goalDays).isEqualTo(1)
        assertThat(summary.totalMinutes).isEqualTo(13)
        // Thursday 2026-11-05: Monday to Wednesday before, Friday to Sunday ahead.
        assertThat(summary.week.map { it.goalMet }).containsExactly(false, false, true, false, false, false, false)
            .inOrder()
        assertThat(summary.week.single { it.isToday }.progress).isEqualTo(0.3f)
        assertThat(summary.week.count { it.isFuture }).isEqualTo(3)
    }

    @Test
    fun `changing the goal applies to today but not to past days`() = runTest {
        repository.addStudyTime(clock.today - 1, 6 * 60_000, goalMillis = 600_000)
        studyTime(6 * 60_000)
        settings.setDailyGoalMinutes(5)
        val summary = observe().first()
        assertThat(summary.today.goalMet).isTrue()
        assertThat(summary.goalDays).isEqualTo(1)
        assertThat(summary.achievements.first { it.type == AchievementType.GoalDays }.value).isEqualTo(1)
    }
}
