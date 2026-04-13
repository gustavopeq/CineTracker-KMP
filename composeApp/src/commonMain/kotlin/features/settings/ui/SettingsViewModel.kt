package features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.model.AuthState
import auth.repository.AuthRepository
import common.util.platform.AppNotifications
import features.settings.domain.SettingsInteractor
import features.settings.events.SettingsEvent
import features.settings.ui.model.DEFAULT_AVATAR_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentLanguageDisplay = MutableStateFlow("")
    val currentLanguageDisplay: StateFlow<String> = _currentLanguageDisplay

    private val _currentRegionDisplay = MutableStateFlow("")
    val currentRegionDisplay: StateFlow<String> = _currentRegionDisplay

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _currentAvatarKey = MutableStateFlow(DEFAULT_AVATAR_KEY)
    val currentAvatarKey: StateFlow<String> = _currentAvatarKey

    val authState: StateFlow<AuthState> = authRepository.authState

    init {
        refreshSettings()
        viewModelScope.launch {
            settingsInteractor.settingsChanged.collect { refreshSettings() }
        }
        viewModelScope.launch {
            authRepository.authState.collect { refreshSettings() }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NotificationPermissionResult -> handlePermissionResult(event.granted)
            SettingsEvent.DisableNotifications -> disableNotifications()
            SettingsEvent.SignOut -> signOut()
            is SettingsEvent.DeleteAccount -> deleteAccount()
        }
    }

    private fun refreshSettings() {
        val currentLanguage = settingsInteractor.getAppLanguage()
        _currentLanguageDisplay.value = settingsInteractor.getSupportedLanguages()
            .find { it.tag == currentLanguage }?.displayName ?: currentLanguage

        val currentRegion = settingsInteractor.getAppRegion()
        _currentRegionDisplay.value = settingsInteractor.getSupportedRegions()
            .find { it.code == currentRegion }?.displayName ?: currentRegion

        _notificationsEnabled.value = settingsInteractor.areNotificationsEnabled()
        _currentAvatarKey.value = settingsInteractor.getUserAvatar()
    }

    private fun handlePermissionResult(granted: Boolean) {
        if (granted) {
            settingsInteractor.setNotificationsEnabled(true)
            AppNotifications.scheduleEngagementReminders()
            _notificationsEnabled.value = true
        }
    }

    private fun disableNotifications() {
        settingsInteractor.setNotificationsEnabled(false)
        AppNotifications.cancelEngagementReminders()
        _notificationsEnabled.value = false
    }

    private fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signOut()
            _isLoading.value = false
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.deleteAccount()
            _isLoading.value = false
        }
    }
}
