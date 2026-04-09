package features.settings.events

sealed class SettingsEvent {
    data class NotificationPermissionResult(val granted: Boolean) : SettingsEvent()
    data object DisableNotifications : SettingsEvent()
    data object SignOut : SettingsEvent()
    data class DeleteAccount(val keepLocalData: Boolean) : SettingsEvent()
}
