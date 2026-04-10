package database.repository

interface SettingsRepository {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
    fun hasSeenDetailsOverlay(): Boolean
    fun setDetailsOverlaySeen()
    fun areEngagementRemindersEnabled(): Boolean
    fun setEngagementRemindersEnabled(enabled: Boolean)
    fun getAppLanguage(): String?
    fun setAppLanguage(languageTag: String)
    fun getAppRegion(): String?
    fun setAppRegion(regionCode: String)
    fun getUserAvatar(): String?
    fun setUserAvatar(avatarKey: String)
    fun clearUserAvatar()
}
