package database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import database.AppDatabase
import database.instantiateImpl
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

actual fun databaseModule(): Module {
    return module {
        single<AppDatabase> { createRoomDatabase() }
    }
}

private fun createRoomDatabase(): AppDatabase {
    val dbFilePath = "${fileDirectory()}/movie_manager_database"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
        factory = { AppDatabase::class.instantiateImpl() },
    )
        .addCallback(roomCallback)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true)
        .build()
}

@OptIn(ExperimentalForeignApi::class)
private fun fileDirectory(): String {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
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
    // Execute the SQL to insert the default lists
    val defaultLists = listOf(
        DefaultLists.WATCHLIST.name.lowercase(),
        DefaultLists.WATCHED.name.lowercase(),
    )
    val query = """
        INSERT INTO list_entity (listName) VALUES
        ('${defaultLists[0]}'),
        ('${defaultLists[1]}')
    """.trimIndent()
    connection.execSQL(query)
}
