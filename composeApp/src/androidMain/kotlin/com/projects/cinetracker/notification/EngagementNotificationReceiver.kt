package com.projects.cinetracker.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.projects.cinetracker.MainActivity
import com.projects.cinetracker.R
import common.util.EngagementMessages
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

class EngagementNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dayOfWeek = intent.getIntExtra(EXTRA_DAY_OF_WEEK, -1)
        val messageRes = EngagementMessages.getRandomMessageForDayOfWeek(dayOfWeek) ?: return

        val messageText = runBlocking { getString(messageRes) }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(messageText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(dayOfWeek, notification)
    }

    companion object {
        const val CHANNEL_ID = "engagement_reminders"
        const val EXTRA_DAY_OF_WEEK = "extra_day_of_week"
    }
}
