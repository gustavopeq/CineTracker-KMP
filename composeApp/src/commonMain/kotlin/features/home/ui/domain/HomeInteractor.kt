package features.home.ui.domain

import common.domain.models.content.GenericContent
import common.domain.models.content.toGenericContent
import common.domain.models.person.PersonDetails
import common.domain.models.person.toPersonDetails
import common.util.DateUtils
import features.home.ui.state.HomeState
import network.repository.home.HomeRepository
import network.util.Left
import network.util.Right

class HomeInteractor(
    private val homeRepository: HomeRepository,
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
