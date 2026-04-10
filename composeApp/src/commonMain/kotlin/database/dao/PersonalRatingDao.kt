package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.model.PersonalRatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRatingDao {
    @Query("SELECT * FROM personal_ratings WHERE contentId = :contentId")
    fun getRating(contentId: Int): Flow<PersonalRatingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: PersonalRatingEntity)

    @Query("DELETE FROM personal_ratings WHERE contentId = :contentId")
    suspend fun deleteRating(contentId: Int)

    @Query("SELECT * FROM personal_ratings")
    fun getAllRatings(): Flow<List<PersonalRatingEntity>>

    @Query("SELECT * FROM personal_ratings")
    suspend fun getAllSnapshot(): List<PersonalRatingEntity>

    @Query("DELETE FROM personal_ratings")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ratings: List<PersonalRatingEntity>)
}
