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
        language: String,
        region: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun getMovieDetailsById(movieId: Int, language: String): Flow<Either<MovieResponse, ApiError>>

    suspend fun getMovieCreditsById(movieId: Int, language: String): Flow<Either<ContentCreditsResponse, ApiError>>

    suspend fun getMovieVideosById(movieId: Int, language: String): Flow<Either<VideosByIdResponse, ApiError>>

    suspend fun getRecommendationsMoviesById(
        movieId: Int,
        language: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun getSimilarMoviesById(
        movieId: Int,
        language: String
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun getStreamingProviders(movieId: Int): Flow<Either<WatchProvidersResponse, ApiError>>
}
