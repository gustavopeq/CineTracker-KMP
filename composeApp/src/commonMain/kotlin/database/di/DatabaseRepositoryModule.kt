package database.di

import database.repository.DatabaseRepository
import database.repository.DatabaseRepositoryImpl
import org.koin.dsl.module

val databaseRepositoryModule = module {
    single<DatabaseRepository> { DatabaseRepositoryImpl(get(), get()) }
}
