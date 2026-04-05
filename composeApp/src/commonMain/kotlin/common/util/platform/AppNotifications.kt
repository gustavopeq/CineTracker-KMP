package common.util.platform

expect object AppNotifications {
    fun scheduleEngagementReminders()
    fun cancelEngagementReminders()
}
