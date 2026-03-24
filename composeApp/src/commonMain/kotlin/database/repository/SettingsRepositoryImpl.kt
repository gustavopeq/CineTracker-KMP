package database.repository

import database.dao.SettingsDao
import database.model.SettingsEntity

class SettingsRepositoryImpl(private val settingsDao: SettingsDao) : SettingsRepository {

    override suspend fun hasCompletedOnboarding(): Boolean =
        settingsDao.getSetting(KEY_ONBOARDING_COMPLETED)?.value == VALUE_TRUE

    override suspend fun setOnboardingCompleted() {
        settingsDao.insertSetting(
            SettingsEntity(
                key = KEY_ONBOARDING_COMPLETED,
                value = VALUE_TRUE
            )
        )
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val VALUE_TRUE = "true"
    }
}
