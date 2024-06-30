package network.services.movie

import core.LanguageManager.getUserLanguageTag
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
        language: String = getUserLanguageTag(),
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun getMovieDetailsById(
        movieId: Int,
        language: String = getUserLanguageTag(),
    ): ApiResult<MovieResponse>

    suspend fun getMovieCreditsById(
        movieId: Int,
        language: String = getUserLanguageTag(),
    ): ApiResult<ContentCreditsResponse>

    suspend fun getMovieVideosById(
        movieId: Int,
        language: String = getUserLanguageTag(),
    ): ApiResult<VideosByIdResponse>

    suspend fun getRecommendationsMoviesById(
        movieId: Int,
        language: String = getUserLanguageTag(),
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun getSimilarMoviesById(
        movieId: Int,
        language: String = getUserLanguageTag(),
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun getStreamingProviders(
        movieId: Int,
        language: String = getUserLanguageTag(),
    ): ApiResult<WatchProvidersResponse>
}
