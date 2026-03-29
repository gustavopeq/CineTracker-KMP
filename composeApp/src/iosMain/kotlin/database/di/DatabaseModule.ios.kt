package database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.AppDatabase
import database.migration.MIGRATION_1_2
import database.migration.MIGRATION_2_3
import database.migration.MIGRATION_3_4
import database.migration.MIGRATION_4_5
import database.migration.MIGRATION_5_6
import database.migration.MIGRATION_6_7
import database.migration.MIGRATION_7_8
import features.watchlist.ui.model.DefaultLists
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun databaseModule(): Module = module {
    single<AppDatabase> { createRoomDatabase() }
}

private fun createRoomDatabase(): AppDatabase {
    val dbFilePath = "${fileDirectory()}/movie_manager_database"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
        .addCallback(roomCallback)
        .addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7,
            MIGRATION_7_8
        )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@OptIn(ExperimentalForeignApi::class)
private fun fileDirectory(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    return requireNotNull(documentDirectory).path!!
}

val roomCallback = object : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        createDefaultLists(connection)
    }
}

private fun createDefaultLists(connection: SQLiteConnection) {
    val defaultLists = listOf(
        DefaultLists.WATCHLIST.name.lowercase(),
        DefaultLists.WATCHED.name.lowercase()
    )
    defaultLists.forEach { listName ->
        val stmt = connection.prepare("INSERT INTO list_entity (listName, isDefault) VALUES (?, 1)")
        stmt.bindText(1, listName)
        stmt.step()
        stmt.close()
    }
}
