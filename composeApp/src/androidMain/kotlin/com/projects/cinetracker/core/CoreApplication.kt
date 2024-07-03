package com.projects.cinetracker.core

import android.app.Application
import core.di.KoinInitializer

class CoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(this@CoreApplication).init()
    }
}
