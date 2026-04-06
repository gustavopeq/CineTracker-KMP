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

class HomeRepositoryImpl(private val homeService: HomeService) : HomeRepository {
    override suspend fun getTrendingMulti(
        language: String
    ): Flow<Either<ContentPagingResponse<MultiResponse>, ApiError>> =
        homeService.getDayTrendingMulti(language = language).asFlow()

    override suspend fun getTrendingPerson(
        language: String
    ): Flow<Either<ContentPagingResponse<PersonResponse>, ApiError>> =
        homeService.getDayTrendingPerson(language = language).asFlow()

    override suspend fun getMoviesComingSoon(
        language: String,
        region: String,
        releaseDateGte: String,
        releaseDateLte: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>> = homeService.getMoviesComingSoon(
        language = language,
        region = region,
        releaseDateGte = releaseDateGte,
        releaseDateLte = releaseDateLte
    ).asFlow()
}
