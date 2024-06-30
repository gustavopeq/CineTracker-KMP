package network.repository.show

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.ShowResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.util.Either

interface ShowRepository {
    suspend fun getShowList(
        contentListType: String,
        pageIndex: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>>

    suspend fun getShowDetailsById(
        showId: Int,
    ): Flow<Either<ShowResponse, ApiError>>

    suspend fun getShowCreditsById(
        showId: Int,
    ): Flow<Either<ContentCreditsResponse, ApiError>>

    suspend fun getShowVideosById(
        showId: Int,
    ): Flow<Either<VideosByIdResponse, ApiError>>

    suspend fun getRecommendationsShowsById(
        showId: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>>

    suspend fun getSimilarShowsById(
        showId: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>>

    suspend fun getStreamingProviders(
        showId: Int,
    ): Flow<Either<WatchProvidersResponse, ApiError>>
}
