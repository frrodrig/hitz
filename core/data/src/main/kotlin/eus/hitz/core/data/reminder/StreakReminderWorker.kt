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
import eus.hitz.core.domain.gamification.ObservePlayerUseCase
import kotlinx.coroutines.flow.first

/**
 * Daily check: if the learner has not practiced today, a notification invites them to keep the
 * streak. It does nothing when notifications are not allowed.
 */
internal class StreakReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun observePlayer(): ObservePlayerUseCase
    }

    override suspend fun doWork(): Result {
        val player = EntryPointAccessors.fromApplication(applicationContext, Dependencies::class.java)
            .observePlayer()()
            .first()
        if (!player.practicedToday) notify(player.streak)
        return Result.success()
    }

    private fun notify(streak: Int) {
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
        val text = if (streak > 0) {
            context.resources.getQuantityString(R.plurals.core_data_reminder_streak, streak, streak)
        } else {
            context.getString(R.string.core_data_reminder_start)
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
        const val CHANNEL_ID = "streak_reminder"
        const val WORK_NAME = "streak_reminder"
        private const val NOTIFICATION_ID = 1
    }
}
