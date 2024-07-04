package database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import common.util.platform.DateUtils

@Entity(
    tableName = "content_entity",
    foreignKeys = [
        ForeignKey(
            entity = ListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ContentEntity(
    @PrimaryKey(autoGenerate = true) val contentEntityDbId: Int = 0,
    val contentId: Int,
    val mediaType: String,
    val listId: Int,
    val createdAt: Long = DateUtils.getCurrentTimeMillis(),
)
