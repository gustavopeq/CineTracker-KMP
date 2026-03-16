package database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_ratings")
data class PersonalRatingEntity(
    @PrimaryKey
    val contentId: Int,
    val mediaType: String,
    val rating: Float,
)
