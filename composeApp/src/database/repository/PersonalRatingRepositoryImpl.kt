package database.repository

import common.domain.models.util.MediaType
import database.dao.PersonalRatingDao
import database.model.PersonalRatingEntity

class PersonalRatingRepositoryImpl(
    private val personalRatingDao: PersonalRatingDao
) : PersonalRatingRepository {

    override suspend fun getRating(contentId: Int): Float? {
        return personalRatingDao.getRating(contentId)?.rating
    }

    override suspend fun setRating(contentId: Int, mediaType: MediaType, rating: Float?) {
        if (rating != null) {
            personalRatingDao.insertRating(
                PersonalRatingEntity(
                    contentId = contentId,
                    mediaType = mediaType.name,
                    rating = rating
                )
            )
        } else {
            personalRatingDao.deleteRating(contentId)
        }
    }
}
