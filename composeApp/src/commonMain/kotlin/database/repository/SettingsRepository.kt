package database.repository

interface SettingsRepository {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
    fun hasSeenDetailsOverlay(): Boolean
    fun setDetailsOverlaySeen()
    fun areEngagementRemindersEnabled(): Boolean
    fun setEngagementRemindersEnabled(enabled: Boolean)
}
