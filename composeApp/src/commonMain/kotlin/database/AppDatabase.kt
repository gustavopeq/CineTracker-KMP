package database

import androidx.room.Database
import androidx.room.RoomDatabase
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.dao.PersonalRatingDao
import database.model.ContentEntity
import database.model.ListEntity
import database.model.PersonalRatingEntity

@Database(
    entities = [ContentEntity::class, ListEntity::class, PersonalRatingEntity::class],
    version = 6,
)
abstract class AppDatabase : RoomDatabase(), DB {
    abstract fun contentEntityDao(): ContentEntityDao
    abstract fun listEntityDao(): ListEntityDao
    abstract fun personalRatingDao(): PersonalRatingDao

    override fun clearAllTables() {
        super.clearAllTables()
    }
}

interface DB {
    fun clearAllTables() {}
}
