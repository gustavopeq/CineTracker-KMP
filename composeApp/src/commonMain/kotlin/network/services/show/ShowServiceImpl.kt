package network.services.show

import com.projects.moviemanager.network.util.Parameters
import io.ktor.client.HttpClient
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.ShowResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult
import network.util.buildUrl
import network.util.getResult

class ShowServiceImpl(
    private val client: HttpClient,
) : ShowService {

    override suspend fun getShowList(
        contentListType: String,
        pageIndex: Int,
        language: String,
    ): ApiResult<ContentPagingResponse<ShowResponse>> {
        val path = "tv/$contentListType"
        val url = buildUrl(path) {
            mapOf(
                Parameters.PAGE_INDEX to pageIndex.toString(),
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getShowDetailsById(
        showId: Int,
        language: String,
    ): ApiResult<ShowResponse> {
        val path = "tv/$showId"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getShowCreditsById(
        showId: Int,
        language: String,
    ): ApiResult<ContentCreditsResponse> {
        val path = "tv/$showId/aggregate_credits"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getShowVideosById(
        showId: Int,
        language: String,
    ): ApiResult<VideosByIdResponse> {
        val path = "tv/$showId/videos"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getRecommendationsShowsById(
        showId: Int,
        language: String,
    ): ApiResult<ContentPagingResponse<ShowResponse>> {
        val path = "tv/$showId/recommendations"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getSimilarShowsById(
        showId: Int,
        language: String,
    ): ApiResult<ContentPagingResponse<ShowResponse>> {
        val path = "tv/$showId/similar"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getStreamingProviders(
        showId: Int,
        language: String,
    ): ApiResult<WatchProvidersResponse> {
        val path = "tv/$showId/watch/providers"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }
}
