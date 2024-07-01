package database

import androidx.room.Database
import androidx.room.RoomDatabase
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.model.ContentEntity
import database.model.ListEntity

@Database(
    entities = [ContentEntity::class, ListEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase(), DB {
    abstract fun contentEntityDao(): ContentEntityDao
    abstract fun listEntityDao(): ListEntityDao

    override fun clearAllTables() {
        super.clearAllTables()
    }
}

interface DB {
    fun clearAllTables() {}
}
