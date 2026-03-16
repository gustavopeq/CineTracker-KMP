package database.di

import database.AppDatabase
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.dao.PersonalRatingDao
import org.koin.dsl.module

val daoModule = module {
    single { provideContentEntityDao(get()) }
    single { provideListEntityDao(get()) }
    single { providePersonalRatingDao(get()) }
}

private fun provideContentEntityDao(appDatabase: AppDatabase): ContentEntityDao {
    return appDatabase.contentEntityDao()
}

private fun provideListEntityDao(appDatabase: AppDatabase): ListEntityDao {
    return appDatabase.listEntityDao()
}

private fun providePersonalRatingDao(appDatabase: AppDatabase): PersonalRatingDao {
    return appDatabase.personalRatingDao()
}
