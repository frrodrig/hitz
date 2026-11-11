package eus.hitz.core.data.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import eus.hitz.core.data.R
import eus.hitz.core.domain.gamification.DailyProgress
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import kotlinx.coroutines.flow.first

/**
 * Daily check: while today's goal is not met, a notification says how many minutes are left.
 * It does nothing when the goal is met or notifications are not allowed.
 */
internal class DailyGoalReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun observePlayer(): ObservePlayerUseCase
    }

    override suspend fun doWork(): Result {
        val today = EntryPointAccessors.fromApplication(applicationContext, Dependencies::class.java)
            .observePlayer()()
            .first()
            .today
        if (!today.goalMet) notify(today)
        return Result.success()
    }

    private fun notify(today: DailyProgress) {
        val context = applicationContext
        val allowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!allowed) return
        createChannel(context)
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val tap = launch?.let {
            PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val left = today.minutesLeft
        val text = if (today.activeMillis > 0) {
            context.resources.getQuantityString(R.plurals.core_data_reminder_left, left, left)
        } else {
            context.resources.getQuantityString(R.plurals.core_data_reminder_start, left, left)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.core_data_ic_notification)
            .setContentTitle(context.getString(R.string.core_data_reminder_title))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(tap)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.core_data_reminder_channel),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "daily_goal_reminder"
        const val WORK_NAME = "daily_goal_reminder"

        /** Work and channel of the streak reminder this one replaces. */
        const val LEGACY_NAME = "streak_reminder"
        private const val NOTIFICATION_ID = 1
    }
}
