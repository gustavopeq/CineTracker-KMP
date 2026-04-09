package features.settings.events

sealed class SettingsEvent {
    data class NotificationPermissionResult(val granted: Boolean) : SettingsEvent()
    data object DisableNotifications : SettingsEvent()
}
