package network.services.movie

import com.projects.moviemanager.network.util.Parameters.LANGUAGE
import com.projects.moviemanager.network.util.Parameters.PAGE_INDEX
import io.ktor.client.HttpClient
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.MovieResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult
import network.util.buildUrl
import network.util.getResult

class MovieServiceImpl(
    private val client: HttpClient,
) : MovieService {

    override suspend fun getMovieList(
        movieListType: String,
        pageIndex: Int,
        language: String,
    ): ApiResult<ContentPagingResponse<MovieResponse>> {
        val path = "movie/$movieListType"
        val url = buildUrl(path) {
            mapOf(
                PAGE_INDEX to pageIndex.toString(),
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getMovieDetailsById(
        movieId: Int,
        language: String,
    ): ApiResult<MovieResponse> {
        val path = "movie/$movieId"
        val url = buildUrl(path) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getMovieCreditsById(
        movieId: Int,
        language: String,
    ): ApiResult<ContentCreditsResponse> {
        val path = "movie/$movieId/credits"
        val url = buildUrl(path) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getMovieVideosById(
        movieId: Int,
        language: String,
    ): ApiResult<VideosByIdResponse> {
        val path = "movie/$movieId/videos"
        val url = buildUrl(path) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getRecommendationsMoviesById(
        movieId: Int,
        language: String,
    ): ApiResult<ContentPagingResponse<MovieResponse>> {
        val path = "movie/$movieId/recommendations"
        val url = buildUrl(path) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getSimilarMoviesById(
        movieId: Int,
        language: String,
    ): ApiResult<ContentPagingResponse<MovieResponse>> {
        val path = "movie/$movieId/similar"
        val url = buildUrl(path) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getStreamingProviders(
        movieId: Int,
        language: String,
    ): ApiResult<WatchProvidersResponse> {
        val path = "movie/$movieId/watch/providers"
        val url = buildUrl(path) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }
}
