package common.util.platform

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.projects.cinetracker.notification.EngagementNotificationReceiver
import common.util.EngagementMessages
import java.util.Calendar

private var appContext: Context? = null

fun initNotifications(context: Context) {
    appContext = context.applicationContext
    createNotificationChannel(context)
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            EngagementNotificationReceiver.CHANNEL_ID,
            "Engagement Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

actual object AppNotifications {

    actual fun scheduleEngagementReminders() {
        val context = appContext ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        scheduleWeeklyAlarm(
            context = context,
            alarmManager = alarmManager,
            dayOfWeek = Calendar.FRIDAY,
            hour = EngagementMessages.FRIDAY_HOUR,
            requestCode = REQUEST_CODE_FRIDAY
        )
        scheduleWeeklyAlarm(
            context = context,
            alarmManager = alarmManager,
            dayOfWeek = Calendar.SUNDAY,
            hour = EngagementMessages.SUNDAY_HOUR,
            requestCode = REQUEST_CODE_SUNDAY
        )
    }

    actual fun cancelEngagementReminders() {
        val context = appContext ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        cancelAlarm(context, alarmManager, Calendar.FRIDAY, REQUEST_CODE_FRIDAY)
        cancelAlarm(context, alarmManager, Calendar.SUNDAY, REQUEST_CODE_SUNDAY)
    }

    private fun scheduleWeeklyAlarm(
        context: Context,
        alarmManager: AlarmManager,
        dayOfWeek: Int,
        hour: Int,
        requestCode: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, EngagementMessages.NOTIFICATION_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, EngagementNotificationReceiver::class.java).apply {
            putExtra(EngagementNotificationReceiver.EXTRA_DAY_OF_WEEK, dayOfWeek)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    private fun cancelAlarm(context: Context, alarmManager: AlarmManager, dayOfWeek: Int, requestCode: Int) {
        val intent = Intent(context, EngagementNotificationReceiver::class.java).apply {
            putExtra(EngagementNotificationReceiver.EXTRA_DAY_OF_WEEK, dayOfWeek)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private const val REQUEST_CODE_FRIDAY = 1001
    private const val REQUEST_CODE_SUNDAY = 1002
}
