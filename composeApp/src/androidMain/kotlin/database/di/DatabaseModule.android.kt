package database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import database.AppDatabase
import database.migration.MIGRATION_1_2
import database.migration.MIGRATION_2_3
import database.migration.MIGRATION_3_4
import database.migration.MIGRATION_4_5
import database.migration.MIGRATION_5_6
import database.migration.MIGRATION_6_7
import database.migration.MIGRATION_7_8
import features.watchlist.ui.model.DefaultLists
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun databaseModule(): Module = module {
    single<AppDatabase> { createRoomDatabase(get()) }
}

private fun createRoomDatabase(context: Context): AppDatabase = Room
    .databaseBuilder(
        context,
        AppDatabase::class.java,
        "movie_manager_database"
    )
    .addMigrations(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8
    )
    .setQueryCoroutineContext(Dispatchers.IO)
    .addCallback(roomCallback)
    .build()

val roomCallback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        createDefaultLists(db)
    }

    private fun createDefaultLists(db: SupportSQLiteDatabase) {
        // Execute the SQL to insert the default lists
        val defaultLists = listOf(
            DefaultLists.WATCHLIST.name.lowercase(),
            DefaultLists.WATCHED.name.lowercase()
        )
        defaultLists.forEach { listName ->
            db.execSQL(
                "INSERT INTO list_entity (listName, isDefault) VALUES (?, 1)",
                arrayOf(listName)
            )
        }
    }
}
