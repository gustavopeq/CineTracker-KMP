package database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import database.AppDatabase
import features.watchlist.ui.model.DefaultLists
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
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(roomCallback)
        .fallbackToDestructiveMigration(true)
        .build()
}

val roomCallback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        createDefaultLists(db)
    }

    private fun createDefaultLists(db: SupportSQLiteDatabase) {
        // Execute the SQL to insert the default lists
        val defaultLists = listOf(
            DefaultLists.WATCHLIST.toString().lowercase(),
            DefaultLists.WATCHED.toString().lowercase(),
        )
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
