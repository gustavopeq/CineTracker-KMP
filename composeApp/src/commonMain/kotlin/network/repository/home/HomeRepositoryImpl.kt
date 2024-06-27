package network.repository.home

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.search.ContentPagingResponse
import network.services.home.HomeService
import network.util.Either
import network.util.asFlow

class HomeRepositoryImpl(
    private val homeService: HomeService,
) : HomeRepository {
    override suspend fun getTrendingMulti(): Flow<Either<ContentPagingResponse<MultiResponse>, ApiError>> {
        return homeService.getDayTrendingMulti().asFlow()
    }

    override suspend fun getTrendingPerson(): Flow<Either<ContentPagingResponse<PersonResponse>, ApiError>> {
        return homeService.getDayTrendingPerson().asFlow()
    }

    override suspend fun getMoviesComingSoon(
        releaseDateGte: String,
        releaseDateLte: String,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>> {
        return homeService.getMoviesComingSoon(
            releaseDateGte = releaseDateGte,
            releaseDateLte = releaseDateLte,
        ).asFlow()
    }
}
