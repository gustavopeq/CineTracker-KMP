package database.di

import database.repository.DatabaseRepository
import database.repository.DatabaseRepositoryImpl
import database.repository.PersonalRatingRepository
import database.repository.PersonalRatingRepositoryImpl
import org.koin.dsl.module

val databaseRepositoryModule = module {
    single<DatabaseRepository> { DatabaseRepositoryImpl(get(), get()) }
    single<PersonalRatingRepository> { PersonalRatingRepositoryImpl(get()) }
}
