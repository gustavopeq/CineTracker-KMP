package com.projects.cinetracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import common.util.platform.AppNotifications
import common.util.platform.initNotifications
import database.repository.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val settingsRepository: SettingsRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        initNotifications(context)
        if (settingsRepository.areEngagementRemindersEnabled()) {
            AppNotifications.scheduleEngagementReminders()
        }
    }
}
