package eus.hitz.core.data.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import eus.hitz.core.domain.settings.ReminderScheduler
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/** Runs [DailyGoalReminderWorker] once a day, around [REMINDER_TIME] local time. */
internal class WorkManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {
    override fun schedule() {
        val request = PeriodicWorkRequestBuilder<DailyGoalReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntilNextReminder(ZonedDateTime.now()).toMillis(), TimeUnit.MILLISECONDS)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(DailyGoalReminderWorker.LEGACY_NAME)
        workManager.enqueueUniquePeriodicWork(
            DailyGoalReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(DailyGoalReminderWorker.WORK_NAME)
    }

    companion object {
        val REMINDER_TIME: LocalTime = LocalTime.of(19, 0)

        internal fun delayUntilNextReminder(now: ZonedDateTime): Duration {
            val today = now.with(REMINDER_TIME)
            val next = if (today.isAfter(now)) today else today.plusDays(1)
            return Duration.between(now, next)
        }
    }
}
