package database.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun settingsModule(): Module = module {
    single<Settings> { createSettings(get()) }
}

private fun createSettings(context: Context): Settings =
    SharedPreferencesSettings(
        context.getSharedPreferences("cinetracker_settings", Context.MODE_PRIVATE)
    )
