package database.repository

import common.domain.models.util.MediaType

interface PersonalRatingRepository {
    suspend fun getRating(contentId: Int): Float?
    suspend fun setRating(contentId: Int, mediaType: MediaType, rating: Float?)
}
