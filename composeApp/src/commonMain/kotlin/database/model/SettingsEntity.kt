package database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_entity")
data class SettingsEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
