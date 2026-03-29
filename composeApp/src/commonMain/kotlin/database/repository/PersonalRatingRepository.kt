package database.repository

import common.domain.models.util.MediaType
import kotlinx.coroutines.flow.Flow

interface PersonalRatingRepository {
    fun getRating(contentId: Int): Flow<Float?>
    fun getAllRatings(): Flow<Map<Int, Float>>
    suspend fun setRating(contentId: Int, mediaType: MediaType, rating: Float?)
}
