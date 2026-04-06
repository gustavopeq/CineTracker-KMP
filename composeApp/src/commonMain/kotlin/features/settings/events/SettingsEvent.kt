package features.settings.events

sealed class SettingsEvent {
    data object LoadSettings : SettingsEvent()
    data class NotificationToggled(val enabled: Boolean) : SettingsEvent()
}
