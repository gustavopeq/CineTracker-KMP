package features.onboarding.ui

import androidx.lifecycle.ViewModel
import common.util.platform.AppNotifications
import database.repository.SettingsRepository

class OnboardingViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    fun enableEngagementReminders() {
        settingsRepository.setEngagementRemindersEnabled(true)
        AppNotifications.scheduleEngagementReminders()
    }

    fun skipEngagementReminders() {
        settingsRepository.setEngagementRemindersEnabled(false)
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        settingsRepository.setOnboardingCompleted()
        onComplete()
    }
}
