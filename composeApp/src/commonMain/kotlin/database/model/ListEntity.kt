package database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_entity")
data class ListEntity(
    @PrimaryKey(autoGenerate = true) val listId: Int = 0,
    val listName: String,
)
