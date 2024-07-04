package features.home.domain

import common.domain.models.content.GenericContent
import common.domain.models.content.toGenericContent
import common.domain.models.person.PersonDetails
import common.domain.models.person.toPersonDetails
import common.domain.models.util.MediaType
import common.util.platform.DateUtils
import database.repository.DatabaseRepository
import features.home.ui.state.HomeState
import features.watchlist.ui.model.DefaultLists
import network.models.content.common.MovieResponse
import network.models.content.common.ShowResponse
import network.repository.home.HomeRepository
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import network.util.Left
import network.util.Right

class HomeInteractor(
    private val homeRepository: HomeRepository,
    private val databaseRepository: DatabaseRepository,
    private val movieRepository: MovieRepository,
    private val showRepository: ShowRepository,
) {
    suspend fun getTrendingMulti(): HomeState {
        val homeState = HomeState()
        val result = homeRepository.getTrendingMulti()

        result.collect { response ->
            when (response) {
                is Right -> {
                    println("getTrendingMulti failed with error: ${response.error}")
                    homeState.setError(errorCode = response.error.code)
                }
                is Left -> {
                    homeState.trendingList.value = response.value.results.mapNotNull {
                        it.toGenericContent()
                    }
                }
            }
        }
        return homeState
    }

    suspend fun getAllWatchlist(): List<GenericContent> {
        val result = databaseRepository.getAllItemsByListId(
            listId = DefaultLists.WATCHLIST.listId,
        )

        return result.mapNotNull { contentEntity ->
            getContentDetailsById(
                contentId = contentEntity.contentId,
                mediaType = MediaType.getType(contentEntity.mediaType),
            )
        }
    }

    private suspend fun getContentDetailsById(
        contentId: Int,
        mediaType: MediaType,
    ): GenericContent? {
        val result = when (mediaType) {
            MediaType.MOVIE -> movieRepository.getMovieDetailsById(contentId)
            MediaType.SHOW -> showRepository.getShowDetailsById(contentId)
            else -> return null
        }

        var contentDetails: GenericContent? = null
        result.collect { response ->
            when (response) {
                is Right -> {
                    println("getContentDetailsById failed with error: ${response.error}")
                }
                is Left -> {
                    contentDetails = when (mediaType) {
                        MediaType.MOVIE -> {
                            (response.value as MovieResponse).toGenericContent()
                        }
                        MediaType.SHOW -> {
                            (response.value as ShowResponse).toGenericContent()
                        }
                        else -> return@collect
                    }
                }
            }
        }
        return contentDetails
    }

    suspend fun getTrendingPerson(): List<PersonDetails> {
        val result = homeRepository.getTrendingPerson()

        var listResults: List<PersonDetails> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    println("getTrendingPerson failed with error: ${response.error}")
                }
                is Left -> {
                    listResults = response.value.results.map {
                        it.toPersonDetails()
                    }
                }
            }
        }
        return listResults
    }

    suspend fun getMoviesComingSoon(): List<GenericContent> {
        val (releaseDateGte, releaseDateLte) = DateUtils.getComingSoonDates()

        val result = homeRepository.getMoviesComingSoon(
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
        )

        var listResults: List<GenericContent> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    println("getMoviesComingSoon failed with error: ${response.error}")
                }
                is Left -> {
                    listResults = response.value.results.mapNotNull {
                        it.toGenericContent()
                    }
                }
            }
        }
        return listResults
    }
}
