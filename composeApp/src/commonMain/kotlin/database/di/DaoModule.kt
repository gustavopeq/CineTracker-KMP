package database.di

import database.AppDatabase
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.dao.PersonalRatingDao
import database.dao.SettingsDao
import org.koin.dsl.module

val daoModule = module {
    single { provideContentEntityDao(get()) }
    single { provideListEntityDao(get()) }
    single { providePersonalRatingDao(get()) }
    single { provideSettingsDao(get()) }
}

private fun provideContentEntityDao(appDatabase: AppDatabase): ContentEntityDao = appDatabase.contentEntityDao()

private fun provideListEntityDao(appDatabase: AppDatabase): ListEntityDao = appDatabase.listEntityDao()

private fun providePersonalRatingDao(appDatabase: AppDatabase): PersonalRatingDao = appDatabase.personalRatingDao()

private fun provideSettingsDao(appDatabase: AppDatabase): SettingsDao = appDatabase.settingsDao()
