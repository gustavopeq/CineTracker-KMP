package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.model.SettingsEntity

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings_entity WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(entity: SettingsEntity)
}
