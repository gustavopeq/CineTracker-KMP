package features.home.domain

import co.touchlab.kermit.Logger
import common.domain.models.content.GenericContent
import common.domain.models.content.toGenericContent
import common.domain.models.person.PersonDetails
import common.domain.models.person.toPersonDetails
import common.domain.models.util.MediaType
import common.util.platform.DateUtils
import database.repository.DatabaseRepository
import features.home.ui.state.HomeState
import features.watchlist.ui.model.DefaultLists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import network.repository.home.HomeRepository
import network.util.Left
import network.util.Right

class HomeInteractor(private val homeRepository: HomeRepository, private val databaseRepository: DatabaseRepository) {
    companion object {
        private const val TAG = "HomeInteractor"
    }

    suspend fun getTrendingMulti(): HomeState {
        val homeState = HomeState()
        val result = homeRepository.getTrendingMulti()

        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getTrendingMulti failed with error: ${response.error}" }
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

    fun getWatchlistFlow(): Flow<List<GenericContent>> =
        databaseRepository.getAllItemsByListId(DefaultLists.WATCHLIST.listId).map { entities ->
            entities.map { entity ->
                GenericContent(
                    id = entity.contentId,
                    name = entity.title,
                    rating = entity.voteAverage.toDouble(),
                    overview = "",
                    posterPath = entity.posterPath.orEmpty(),
                    backdropPath = "",
                    mediaType = MediaType.getType(entity.mediaType)
                )
            }
        }

    suspend fun getTrendingPerson(): List<PersonDetails> {
        val result = homeRepository.getTrendingPerson()

        var listResults: List<PersonDetails> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getTrendingPerson failed with error: ${response.error}" }
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
            releaseDateLte = releaseDateLte
        )

        var listResults: List<GenericContent> = emptyList()
        result.collect { response ->
            when (response) {
                is Right -> {
                    Logger.e(TAG) { "getMoviesComingSoon failed with error: ${response.error}" }
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
