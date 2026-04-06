package network.services.show

import network.models.content.common.ContentCreditsResponse
import network.models.content.common.ShowResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult

interface ShowService {
    suspend fun getShowList(
        contentListType: String,
        pageIndex: Int,
        language: String,
        region: String
    ): ApiResult<ContentPagingResponse<ShowResponse>>

    suspend fun getShowDetailsById(showId: Int, language: String): ApiResult<ShowResponse>

    suspend fun getShowCreditsById(
        showId: Int,
        language: String
    ): ApiResult<ContentCreditsResponse>

    suspend fun getShowVideosById(showId: Int, language: String): ApiResult<VideosByIdResponse>

    suspend fun getRecommendationsShowsById(
        showId: Int,
        language: String
    ): ApiResult<ContentPagingResponse<ShowResponse>>

    suspend fun getSimilarShowsById(
        showId: Int,
        language: String
    ): ApiResult<ContentPagingResponse<ShowResponse>>

    suspend fun getStreamingProviders(
        showId: Int
    ): ApiResult<WatchProvidersResponse>
}
