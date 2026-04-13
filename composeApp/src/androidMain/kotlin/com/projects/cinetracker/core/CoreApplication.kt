package com.projects.cinetracker.core

import android.app.Application
import com.projects.cinetracker.BuildKonfig
import common.util.platform.AppNotifications
import common.util.platform.PlatformUtils
import common.util.platform.initNotifications
import core.di.KoinInitializer
import database.repository.SettingsRepository
import io.sentry.kotlin.multiplatform.Sentry
import org.koin.mp.KoinPlatform

class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(this@CoreApplication).init()
        initSentry()
        initNotifications(this@CoreApplication)
        rescheduleEngagementRemindersIfEnabled()
    }

    private fun rescheduleEngagementRemindersIfEnabled() {
        val settingsRepository: SettingsRepository = KoinPlatform.getKoin().get()
        if (settingsRepository.areEngagementRemindersEnabled()) {
            AppNotifications.scheduleEngagementReminders()
        }
    }

    private fun initSentry() {
        val dsn = BuildKonfig.SENTRY_DSN
        if (dsn.isNotEmpty()) {
            Sentry.init { options ->
                options.dsn = dsn
                options.environment = if (PlatformUtils.isDebugBuild) "debug" else "release"
                options.debug = false
            }
        }
    }
}
