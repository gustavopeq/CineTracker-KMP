package features.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.model.AuthState
import auth.repository.AuthRepository
import common.util.platform.AppNotifications
import database.repository.DatabaseRepository
import features.settings.domain.SettingsInteractor
import features.settings.events.SettingsEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val authRepository: AuthRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentLanguageDisplay = MutableStateFlow("")
    val currentLanguageDisplay: StateFlow<String> = _currentLanguageDisplay

    private val _currentRegionDisplay = MutableStateFlow("")
    val currentRegionDisplay: StateFlow<String> = _currentRegionDisplay

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    val authState: StateFlow<AuthState> = authRepository.authState

    init {
        refreshSettings()
        viewModelScope.launch {
            settingsInteractor.settingsChanged.collect { refreshSettings() }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NotificationPermissionResult -> handlePermissionResult(event.granted)
            SettingsEvent.DisableNotifications -> disableNotifications()
            SettingsEvent.SignOut -> signOut()
            is SettingsEvent.DeleteAccount -> deleteAccount(event.keepLocalData)
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

    private fun deleteAccount(keepLocalData: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.deleteAccount()
            if (!keepLocalData) {
                databaseRepository.getAllLists().first().forEach { list ->
                    if (list.isDefault) {
                        databaseRepository.clearList(list.listId)
                    } else {
                        databaseRepository.deleteList(list.listId)
                    }
                }
            }
            _isLoading.value = false
        }
    }
}
