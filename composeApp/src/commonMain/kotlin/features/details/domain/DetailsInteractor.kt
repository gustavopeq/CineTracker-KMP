package features.details.domain

import co.touchlab.kermit.Logger
import common.domain.models.content.GenericContent
import common.domain.models.content.StreamProvider
import common.domain.models.content.Videos
import common.domain.models.content.toContentCast
import common.domain.models.content.toDetailedContent
import common.domain.models.content.toGenericContent
import common.domain.models.content.toGenericContentList
import common.domain.models.content.toStreamProvider
import common.domain.models.content.toVideos
import common.domain.models.list.ListItem
import common.domain.models.person.PersonImage
import common.domain.models.person.toPersonImage
import common.domain.models.util.MediaType
import core.LanguageManager.getUserCountryCode
import database.repository.DatabaseRepository
import database.repository.PersonalRatingRepository
import features.details.state.DetailsState
import features.watchlist.domain.ListInteractor
import kotlinx.coroutines.flow.Flow
import network.models.content.common.BaseContentResponse
import network.models.content.common.MovieResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse
import network.models.content.search.ContentPagingResponse
import network.repository.movie.MovieRepository
import network.repository.person.PersonRepository
import network.repository.show.ShowRepository
import network.util.Left
import network.util.Right

class DetailsInteractor(
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
    private val personRepository: PersonRepository,
    private val listInteractor: ListInteractor,
    private val personalRatingRepository: PersonalRatingRepository,
    private val databaseRepository: DatabaseRepository
) {
    companion object {
        private const val TAG = "DetailsInteractor"
    }
    suspend fun getContentDetailsById(contentId: Int, mediaType: MediaType): DetailsState {
        val detailsState = DetailsState()
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieDetailsById(contentId)
            MediaType.SHOW -> showRepository.getShowDetailsById(contentId)
            MediaType.PERSON -> personRepository.getPersonDetailsById(contentId)
            else -> return detailsState
        }

        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getContentDetailsById failed with error: ${response.error}" }
                    detailsState.setError(errorCode = response.error.code)
                }
                is Left -> {
                    detailsState.detailsInfo.value = when (mediaType) {
                        MediaType.MOVIE -> (response.value as MovieResponse).toDetailedContent()
                        MediaType.SHOW -> (response.value as ShowResponse).toDetailedContent()
                        MediaType.PERSON -> {
                            (response.value as PersonResponse).toDetailedContent()
                        }
                        else -> {
                            null
                        }
                    }

                    val streamProviders = getStreamingProviders(contentId, mediaType)
                    if (streamProviders.isNotEmpty()) {
                        detailsState.detailsInfo.value = detailsState.detailsInfo.value?.copy(
                            streamProviders = streamProviders
                        )
                    }
                }
            }
        }

        return detailsState
    }

    suspend fun getContentCastById(contentId: Int, mediaType: MediaType): DetailsState {
        val detailsState = DetailsState()

        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieCreditsById(contentId)
            MediaType.SHOW -> showRepository.getShowCreditsById(contentId)
            else -> return return detailsState
        }

        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getContentCreditsById failed with error: ${response.error}" }
                    detailsState.setError(errorCode = response.error.code)
                }
                is Left -> {
                    detailsState.detailsCast.value = response.value.cast?.map {
                        it.toContentCast()
                    }?.filterNot {
                        it.profilePoster.isEmpty()
                    }?.sortedBy { it.order ?: Int.MAX_VALUE }.orEmpty()
                }
            }
        }
        return detailsState
    }

    suspend fun getContentVideosById(contentId: Int, mediaType: MediaType): List<Videos> {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieVideosById(contentId)
            MediaType.SHOW -> showRepository.getShowVideosById(contentId)
            else -> return emptyList()
        }

        var videoList: List<Videos> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getContentVideosById failed with error: ${response.error}" }
                }
                is Left -> {
                    videoList = response.value.results?.map {
                        it.toVideos()
                    }.orEmpty()
                }
            }
        }

        return videoList
    }

    suspend fun getRecommendationsContentById(contentId: Int, mediaType: MediaType): List<GenericContent> {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getRecommendationsMoviesById(contentId)
            MediaType.SHOW -> showRepository.getRecommendationsShowsById(contentId)
            else -> return emptyList()
        }

        var listOfSimilar: List<GenericContent> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getRecommendationsContentById failed with error: ${response.error}" }
                }
                is Left -> {
                    listOfSimilar = mapResponseToGenericContent(response)
                    if (listOfSimilar.isEmpty()) {
                        listOfSimilar = getSimilarContentById(
                            contentId = contentId,
                            mediaType = mediaType
                        )
                    }
                }
            }
        }
        return listOfSimilar
    }

    private suspend fun getSimilarContentById(contentId: Int, mediaType: MediaType): List<GenericContent> {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getSimilarMoviesById(contentId)
            MediaType.SHOW -> showRepository.getSimilarShowsById(contentId)
            else -> return emptyList()
        }

        var listOfSimilar: List<GenericContent> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getSimilarContentById failed with error: ${response.error}" }
                }
                is Left -> {
                    listOfSimilar = mapResponseToGenericContent(response)
                }
            }
        }
        return listOfSimilar
    }

    suspend fun getStreamingProviders(contentId: Int, mediaType: MediaType): List<StreamProvider> {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getStreamingProviders(contentId)
            MediaType.SHOW -> showRepository.getStreamingProviders(contentId)
            else -> return emptyList()
        }

        var streamProviderList: List<StreamProvider> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getStreamingProviders failed with error: ${response.error}" }
                }
                is Left -> {
                    val userCountryCode = getUserCountryCode()
                    streamProviderList = response.value.results?.get(
                        userCountryCode
                    )?.flatrate?.map {
                        it.toStreamProvider()
                    }.orEmpty()
                }
            }
        }
        return streamProviderList
    }

    private fun mapResponseToGenericContent(
        response: Left<ContentPagingResponse<out BaseContentResponse>>
    ): List<GenericContent> = response.value.results
        .filter {
            it.poster_path?.isNotEmpty() == true && it.title?.isNotEmpty() == true
        }
        .mapNotNull {
            it.toGenericContent()
        }

    suspend fun getPersonCreditsById(personId: Int): List<GenericContent> {
        val result = personRepository.getPersonCreditsById(personId)

        var mediaContentList: List<GenericContent> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getPersonCreditsById failed with error: ${response.error}" }
                }
                is Left -> {
                    mediaContentList = response.value.cast.toGenericContentList().filterNot {
                        it.name.isEmpty() || it.posterPath.isEmpty()
                    }
                }
            }
        }
        return mediaContentList
    }

    suspend fun getPersonImages(personId: Int): List<PersonImage> {
        val result = personRepository.getPersonImagesById(personId)

        var imageList: List<PersonImage> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getPersonImages failed with error: ${response.error}" }
                }
                is Left -> {
                    imageList = response.value.profiles?.filter {
                        it?.file_path?.isNotEmpty() == true
                    }?.mapNotNull {
                        it?.toPersonImage()
                    } ?: emptyList()
                }
            }
        }
        return imageList
    }

    fun verifyContentInLists(contentId: Int, mediaType: MediaType): Flow<Map<Int, Boolean>> =
        listInteractor.verifyContentInLists(contentId, mediaType)

    suspend fun toggleWatchlist(
        currentStatus: Boolean,
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
        title: String = "",
        posterPath: String? = null,
        voteAverage: Float = 0f
    ) = listInteractor.toggleWatchlist(currentStatus, contentId, mediaType, listId, title, posterPath, voteAverage)

    fun getAllLists(): Flow<List<ListItem>> = listInteractor.getAllLists()

    fun getPersonalRating(contentId: Int): Flow<Float?> = personalRatingRepository.getRating(contentId)

    suspend fun setPersonalRating(contentId: Int, mediaType: MediaType, rating: Float) {
        personalRatingRepository.setRating(contentId, mediaType, rating)
    }

    suspend fun removePersonalRating(contentId: Int, mediaType: MediaType) {
        personalRatingRepository.setRating(contentId, mediaType, null)
    }

    suspend fun updateCachedFields(
        contentId: Int,
        mediaType: MediaType,
        title: String,
        posterPath: String?,
        voteAverage: Float
    ) {
        databaseRepository.updateCachedFields(contentId, mediaType, title, posterPath, voteAverage)
    }
}
