package com.projects.cinetracker.core

import android.app.Application
import di.KoinInitializer

class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}
