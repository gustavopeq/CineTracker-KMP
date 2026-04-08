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
import features.settings.domain.SettingsInteractor
import features.watchlist.ui.model.DefaultLists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import network.repository.home.HomeRepository
import network.util.Left
import network.util.Right

class HomeInteractor(
    private val homeRepository: HomeRepository,
    private val databaseRepository: DatabaseRepository,
    private val settingsInteractor: SettingsInteractor
) {
    companion object {
        private const val TAG = "HomeInteractor"
    }

    suspend fun getTrendingMulti(): HomeState {
        val homeState = HomeState()
        val language = settingsInteractor.getAppLanguage()
        val result = homeRepository.getTrendingMulti(language = language)

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
        val language = settingsInteractor.getAppLanguage()
        val result = homeRepository.getTrendingPerson(language = language)

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
        val language = settingsInteractor.getAppLanguage()
        val region = settingsInteractor.getAppRegion()

        val result = homeRepository.getMoviesComingSoon(
            language = language,
            region = region,
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
