package database.repository

import com.russhwolf.settings.Settings

class SettingsRepositoryImpl(private val settings: Settings) : SettingsRepository {

    override fun hasCompletedOnboarding(): Boolean = settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    override fun setOnboardingCompleted() {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, true)
    }

    override fun hasSeenDetailsOverlay(): Boolean = settings.getBoolean(KEY_DETAILS_OVERLAY_SEEN, false)

    override fun setDetailsOverlaySeen() {
        settings.putBoolean(KEY_DETAILS_OVERLAY_SEEN, true)
    }

    override fun areEngagementRemindersEnabled(): Boolean = settings.getBoolean(KEY_ENGAGEMENT_REMINDERS_ENABLED, false)

    override fun setEngagementRemindersEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_ENGAGEMENT_REMINDERS_ENABLED, enabled)
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_DETAILS_OVERLAY_SEEN = "details_overlay_seen"
        private const val KEY_ENGAGEMENT_REMINDERS_ENABLED = "engagement_reminders_enabled"
    }
}
