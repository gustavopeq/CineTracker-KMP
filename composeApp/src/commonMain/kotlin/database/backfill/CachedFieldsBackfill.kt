package database.backfill

import co.touchlab.kermit.Logger
import common.domain.models.content.GenericContent
import common.domain.models.content.toGenericContent
import common.domain.models.util.MediaType
import database.repository.DatabaseRepository
import network.models.content.common.MovieResponse
import network.models.content.common.ShowResponse
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import network.util.Left

class CachedFieldsBackfill(
    private val databaseRepository: DatabaseRepository,
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository
) {
    companion object {
        private const val TAG = "CachedFieldsBackfill"
    }

    private var hasRun = false

    suspend fun backfillIfNeeded() {
        if (hasRun) return
        hasRun = true
        val staleEntities = databaseRepository.getEntitiesWithMissingCachedFields()
        if (staleEntities.isEmpty()) return

        Logger.i(TAG) { "Backfilling ${staleEntities.size} entities with missing cached fields" }

        val uniqueContent = staleEntities.distinctBy { it.contentId to it.mediaType }

        uniqueContent.forEach { entity ->
            val mediaType = MediaType.getType(entity.mediaType)
            val details = fetchDetails(entity.contentId, mediaType) ?: return@forEach
            databaseRepository.updateCachedFields(
                contentId = entity.contentId,
                mediaType = mediaType,
                title = details.name,
                posterPath = details.posterPath,
                voteAverage = details.rating.toFloat()
            )
        }
    }

    private suspend fun fetchDetails(contentId: Int, mediaType: MediaType): GenericContent? {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieDetailsById(contentId)
            MediaType.SHOW -> showRepository.getShowDetailsById(contentId)
            else -> return null
        }

        var content: GenericContent? = null
        result.collect { response ->
            when (response) {
                is Left -> {
                    content = when (mediaType) {
                        MediaType.MOVIE -> (response.value as MovieResponse).toGenericContent()
                        MediaType.SHOW -> (response.value as ShowResponse).toGenericContent()
                        else -> null
                    }
                }
                else -> {
                    Logger.e(TAG) { "Failed to fetch details for contentId=$contentId" }
                }
            }
        }
        return content
    }
}
