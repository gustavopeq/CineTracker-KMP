package database.repository

import auth.service.SyncService
import common.domain.models.util.MediaType
import database.dao.PersonalRatingDao
import database.model.PersonalRatingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonalRatingRepositoryImpl(
    private val personalRatingDao: PersonalRatingDao,
    private val syncService: SyncService
) : PersonalRatingRepository {

    override fun getRating(contentId: Int): Flow<Float?> = personalRatingDao.getRating(contentId).map { it?.rating }

    override fun getAllRatings(): Flow<Map<Int, Float>> = personalRatingDao.getAllRatings().map { entities ->
        entities.associate { it.contentId to it.rating }
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
        syncService.requestUpload()
    }
}
