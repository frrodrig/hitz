package eus.hitz.core.data.reminder

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime

class WorkManagerReminderSchedulerTest {
    private fun at(hour: Int, minute: Int = 0) = ZonedDateTime.of(2026, 11, 4, hour, minute, 0, 0, ZoneOffset.UTC)

    @Test
    fun `the reminder is today before the reminder time`() {
        assertThat(WorkManagerReminderScheduler.delayUntilNextReminder(at(10))).isEqualTo(Duration.ofHours(9))
    }

    @Test
    fun `the reminder is tomorrow after the reminder time`() {
        assertThat(WorkManagerReminderScheduler.delayUntilNextReminder(at(20, 30)))
            .isEqualTo(Duration.ofHours(22).plusMinutes(30))
    }
}
