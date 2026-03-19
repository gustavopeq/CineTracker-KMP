package database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.dao.PersonalRatingDao
import database.model.ContentEntity
import database.model.ListEntity
import database.model.PersonalRatingEntity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@ConstructedBy(AppDatabaseConstructor::class)
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
