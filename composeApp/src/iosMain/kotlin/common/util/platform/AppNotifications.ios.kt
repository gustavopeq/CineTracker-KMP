package common.util.platform

import common.util.EngagementMessages
import io.sentry.kotlin.multiplatform.Sentry
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

private val notificationDelegate = NotificationResponseDelegate()

private class NotificationResponseDelegate :
    NSObject(),
    UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        val identifier = didReceiveNotificationResponse.notification.request.identifier
        if (identifier == AppNotifications.IDENTIFIER_FRIDAY || identifier == AppNotifications.IDENTIFIER_SUNDAY) {
            Sentry.captureMessage("notification.opened")
        }
        withCompletionHandler()
    }
}

actual object AppNotifications {

    internal const val IDENTIFIER_FRIDAY = "engagement_friday"
    internal const val IDENTIFIER_SUNDAY = "engagement_sunday"

    fun setupNotificationDelegate() {
        UNUserNotificationCenter.currentNotificationCenter().delegate = notificationDelegate
    }

    actual fun scheduleEngagementReminders() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removeAllPendingNotificationRequests()
        Sentry.captureMessage("notification.scheduled")

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

    private fun scheduleWeekly(center: UNUserNotificationCenter, weekday: Long, hour: Long, identifier: String) {
        val messageRes = EngagementMessages.getRandomMessageForDayOfWeek(weekday.toInt()) ?: return
        val messageText = runBlocking { getString(messageRes) }

        val content = UNMutableNotificationContent().apply {
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
}
