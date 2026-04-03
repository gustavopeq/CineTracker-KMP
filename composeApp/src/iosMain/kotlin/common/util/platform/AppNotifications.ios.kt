package common.util.platform

import common.util.EngagementMessages
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

actual object AppNotifications {

    actual fun scheduleEngagementReminders() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removeAllPendingNotificationRequests()

        scheduleWeekly(
            center = center,
            weekday = EngagementMessages.FRIDAY_DAY_OF_WEEK.toLong(),
            hour = EngagementMessages.FRIDAY_HOUR.toLong(),
            identifier = IDENTIFIER_FRIDAY
        )
        scheduleWeekly(
            center = center,
            weekday = EngagementMessages.SUNDAY_DAY_OF_WEEK.toLong(),
            hour = EngagementMessages.SUNDAY_HOUR.toLong(),
            identifier = IDENTIFIER_SUNDAY
        )
    }

    actual fun cancelEngagementReminders() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(
            listOf(IDENTIFIER_FRIDAY, IDENTIFIER_SUNDAY)
        )
    }

    private fun scheduleWeekly(
        center: UNUserNotificationCenter,
        weekday: Long,
        hour: Long,
        identifier: String,
    ) {
        val messageRes = EngagementMessages.getRandomMessageForDayOfWeek(weekday.toInt()) ?: return
        val messageText = runBlocking { getString(messageRes) }

        val content = UNMutableNotificationContent().apply {
            setTitle("CineTracker")
            setBody(messageText)
            setSound(UNNotificationSound.defaultSound())
        }

        val dateComponents = NSDateComponents().apply {
            this.weekday = weekday
            this.hour = hour
            this.minute = EngagementMessages.NOTIFICATION_MINUTE.toLong()
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents,
            repeats = true
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier,
            content = content,
            trigger = trigger
        )

        center.addNotificationRequest(request, withCompletionHandler = null)
    }

    private const val IDENTIFIER_FRIDAY = "engagement_friday"
    private const val IDENTIFIER_SUNDAY = "engagement_sunday"
}
