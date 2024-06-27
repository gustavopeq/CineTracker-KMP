package features.home.ui.domain

import common.domain.models.content.toGenericContent
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
}
