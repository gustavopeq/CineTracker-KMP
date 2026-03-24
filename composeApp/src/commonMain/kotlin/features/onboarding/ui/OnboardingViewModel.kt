package features.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import database.repository.SettingsRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted()
            onComplete()
        }
    }
}
