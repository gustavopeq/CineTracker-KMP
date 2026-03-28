package database.repository

interface SettingsRepository {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
}
