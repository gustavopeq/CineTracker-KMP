package features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.util.platform.AppNotifications
import features.settings.domain.SettingsInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _currentLanguageDisplay = MutableStateFlow("")
    val currentLanguageDisplay: StateFlow<String> = _currentLanguageDisplay

    private val _currentRegionDisplay = MutableStateFlow("")
    val currentRegionDisplay: StateFlow<String> = _currentRegionDisplay

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    init {
        refreshSettings()
        viewModelScope.launch {
            settingsInteractor.settingsChanged.collect { refreshSettings() }
        }
    }

    fun refreshSettings() {
        val currentLanguage = settingsInteractor.getAppLanguage()
        _currentLanguageDisplay.value = settingsInteractor.getSupportedLanguages()
            .find { it.tag == currentLanguage }?.displayName ?: currentLanguage

        val currentRegion = settingsInteractor.getAppRegion()
        _currentRegionDisplay.value = settingsInteractor.getSupportedRegions()
            .find { it.code == currentRegion }?.displayName ?: currentRegion

        _notificationsEnabled.value = settingsInteractor.areNotificationsEnabled()
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        if (granted) {
            settingsInteractor.setNotificationsEnabled(true)
            AppNotifications.scheduleEngagementReminders()
            _notificationsEnabled.value = true
        }
    }

    fun disableNotifications() {
        settingsInteractor.setNotificationsEnabled(false)
        AppNotifications.cancelEngagementReminders()
        _notificationsEnabled.value = false
    }
}
