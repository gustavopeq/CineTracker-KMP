package database.repository

import com.russhwolf.settings.Settings

class SettingsRepositoryImpl(private val settings: Settings) : SettingsRepository {

    override fun hasCompletedOnboarding(): Boolean =
        settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun setOnboardingCompleted() {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, true)
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
