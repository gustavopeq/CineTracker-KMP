package network.repository.home

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.search.ContentPagingResponse
import network.util.Either

interface HomeRepository {
    suspend fun getTrendingMulti(): Flow<Either<ContentPagingResponse<MultiResponse>, ApiError>>
    suspend fun getTrendingPerson(): Flow<Either<ContentPagingResponse<PersonResponse>, ApiError>>
    suspend fun getMoviesComingSoon(
        releaseDateGte: String,
        releaseDateLte: String,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>
}
