package database.di

import database.repository.DatabaseRepository
import database.repository.DatabaseRepositoryImpl
import database.repository.PersonalRatingRepository
import database.repository.PersonalRatingRepositoryImpl
import database.repository.SettingsRepository
import database.repository.SettingsRepositoryImpl
import org.koin.dsl.module

val databaseRepositoryModule = module {
    single<DatabaseRepository> { DatabaseRepositoryImpl(get(), get()) }
    single<PersonalRatingRepository> { PersonalRatingRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}
