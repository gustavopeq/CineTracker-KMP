package features.onboarding.ui

import androidx.lifecycle.ViewModel
import database.repository.SettingsRepository

class OnboardingViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    fun completeOnboarding(onComplete: () -> Unit) {
        settingsRepository.setOnboardingCompleted()
        onComplete()
    }
}
