package com.projects.cinetracker.core

import android.app.Application
import com.projects.cinetracker.BuildKonfig
import common.util.platform.PlatformUtils
import core.di.KoinInitializer
import io.sentry.kotlin.multiplatform.Sentry

class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(this@CoreApplication).init()
        initSentry()
    }

    private fun initSentry() {
        val dsn = BuildKonfig.SENTRY_DSN
        if (dsn.isNotEmpty()) {
            Sentry.init { options ->
                options.dsn = dsn
                options.environment = if (PlatformUtils.isDebugBuild) "debug" else "release"
                options.debug = PlatformUtils.isDebugBuild
            }
        }
    }
}
