package network.services.movie

import network.models.content.common.ContentCreditsResponse
import network.models.content.common.MovieResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult

interface MovieService {
    suspend fun getMovieList(
        movieListType: String,
        pageIndex: Int,
        language: String,
        region: String
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun getMovieDetailsById(movieId: Int, language: String): ApiResult<MovieResponse>

    suspend fun getMovieCreditsById(
        movieId: Int,
        language: String
    ): ApiResult<ContentCreditsResponse>

    suspend fun getMovieVideosById(movieId: Int, language: String): ApiResult<VideosByIdResponse>

    suspend fun getRecommendationsMoviesById(
        movieId: Int,
        language: String
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun getSimilarMoviesById(
        movieId: Int,
        language: String
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun getStreamingProviders(
        movieId: Int
    ): ApiResult<WatchProvidersResponse>
}
