package eus.hitz.core.domain.gamification

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DailyGoalRulesTest {
    @Test
    fun `a step counts at most a minute`() {
        assertThat(DailyGoalRules.step(25_000)).isEqualTo(25_000)
        assertThat(DailyGoalRules.step(10 * 60_000)).isEqualTo(60_000)
        assertThat(DailyGoalRules.step(-5)).isEqualTo(0)
    }

    @Test
    fun `minutes left round up and stop at zero`() {
        assertThat(DailyGoalRules.minutesLeft(StudyDay(0, 0, 600_000))).isEqualTo(10)
        assertThat(DailyGoalRules.minutesLeft(StudyDay(0, 541_000, 600_000))).isEqualTo(1)
        assertThat(DailyGoalRules.minutesLeft(StudyDay(0, 700_000, 600_000))).isEqualTo(0)
    }

    @Test
    fun `a day can go over its goal`() {
        val day = StudyDay(0, 900_000, 600_000)
        assertThat(day.progress).isEqualTo(1.5f)
        assertThat(day.goalMet).isTrue()
    }

    @Test
    fun `weeks start on Monday`() {
        // 2026-11-04 is a Wednesday; its Monday is 2026-11-02.
        val wednesday = java.time.LocalDate.of(2026, 11, 4).toEpochDay()
        assertThat(mondayOf(wednesday)).isEqualTo(java.time.LocalDate.of(2026, 11, 2).toEpochDay())
        assertThat(mondayOf(wednesday - 2)).isEqualTo(wednesday - 2)
    }

    @Test
    fun `achievements count goal days and minutes`() {
        val data = PlayerData(
            studyDays = mapOf(
                1L to StudyDay(1, 600_000, 600_000),
                2L to StudyDay(2, 300_000, 600_000),
                3L to StudyDay(3, 3_000_000, 600_000),
            ),
        )
        val progress = Achievements.progress(data).associateBy { it.type }
        assertThat(progress.getValue(AchievementType.GoalDays).value).isEqualTo(2)
        assertThat(progress.getValue(AchievementType.StudyMinutes).value).isEqualTo(65)
        assertThat(progress.getValue(AchievementType.StudyMinutes).tier).isEqualTo(1)
    }
}
