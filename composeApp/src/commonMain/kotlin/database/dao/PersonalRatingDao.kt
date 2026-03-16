package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.model.PersonalRatingEntity

@Dao
interface PersonalRatingDao {
    @Query("SELECT * FROM personal_ratings WHERE contentId = :contentId")
    suspend fun getRating(contentId: Int): PersonalRatingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: PersonalRatingEntity)

    @Query("DELETE FROM personal_ratings WHERE contentId = :contentId")
    suspend fun deleteRating(contentId: Int)

    @Query("SELECT * FROM personal_ratings")
    suspend fun getAllRatings(): List<PersonalRatingEntity>
}
