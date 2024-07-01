package database.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.AppDatabase
import database.instantiateImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

actual fun databaseModule(): Module {
    return module {
        single<AppDatabase> { createRoomDatabase() }
    }
}

private fun createRoomDatabase(): AppDatabase {
    val dbFilePath = NSHomeDirectory() + "/movie_manager_database"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
        factory = { AppDatabase::class.instantiateImpl() },
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true)
        .build()
}
