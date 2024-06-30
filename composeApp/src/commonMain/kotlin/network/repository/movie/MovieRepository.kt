package network.repository.movie

import common.domain.models.util.ContentListType
import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.MovieResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.util.Either

interface MovieRepository {
    suspend fun getMovieList(
        contentListType: ContentListType,
        pageIndex: Int,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun getMovieDetailsById(
        movieId: Int,
    ): Flow<Either<MovieResponse, ApiError>>

    suspend fun getMovieCreditsById(
        movieId: Int,
    ): Flow<Either<ContentCreditsResponse, ApiError>>

    suspend fun getMovieVideosById(
        movieId: Int,
    ): Flow<Either<VideosByIdResponse, ApiError>>

    suspend fun getRecommendationsMoviesById(
        movieId: Int,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun getSimilarMoviesById(
        movieId: Int,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun getStreamingProviders(
        movieId: Int,
    ): Flow<Either<WatchProvidersResponse, ApiError>>
}
