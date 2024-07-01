package database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun databaseModule(): Module {
    return module {
        single<AppDatabase> { createRoomDatabase(get()) }
    }
}

private fun createRoomDatabase(
    context: Context,
): AppDatabase {
    val dbFile = context.getDatabasePath("movie_manager_database")
    return Room.databaseBuilder<AppDatabase>(
        context,
        dbFile.absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(roomCallback)
        .fallbackToDestructiveMigration(true)
        .build()
}

val roomCallback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        println("printlog - onCreate")
        createDefaultLists(db)
    }

    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
        super.onDestructiveMigration(db)
        println("printlog - onDestructive")
        createDefaultLists(db)
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        println("printlog - onOpen")
    }

    private fun createDefaultLists(db: SupportSQLiteDatabase) {
        // Execute the SQL to insert the default lists
        val defaultLists = listOf("watchlist", "watched")
        defaultLists.forEach { listName ->
            db.execSQL(
                """
                    INSERT INTO list_entity (listName)
                    VALUES (?)
                    """,
                arrayOf(listName),
            )
        }
    }
}
