package network.repository.show

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.ContentCreditsResponse
import network.models.content.common.ShowResponse
import network.models.content.common.VideosByIdResponse
import network.models.content.common.WatchProvidersResponse
import network.models.content.search.ContentPagingResponse
import network.services.show.ShowService
import network.util.Either
import network.util.asFlow

class ShowRepositoryImpl(private val showService: ShowService) : ShowRepository {
    override suspend fun getShowList(
        contentListType: String,
        pageIndex: Int,
        language: String,
        region: String
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> = showService.getShowList(
        contentListType = contentListType,
        pageIndex = pageIndex,
        language = language,
        region = region
    ).asFlow()

    override suspend fun getShowDetailsById(showId: Int, language: String): Flow<Either<ShowResponse, ApiError>> =
        showService.getShowDetailsById(
            showId = showId,
            language = language
        ).asFlow()

    override suspend fun getShowCreditsById(
        showId: Int,
        language: String
    ): Flow<Either<ContentCreditsResponse, ApiError>> = showService.getShowCreditsById(
        showId = showId,
        language = language
    ).asFlow()

    override suspend fun getShowVideosById(showId: Int, language: String): Flow<Either<VideosByIdResponse, ApiError>> =
        showService.getShowVideosById(
            showId = showId,
            language = language
        ).asFlow()

    override suspend fun getRecommendationsShowsById(
        showId: Int,
        language: String
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> = showService.getRecommendationsShowsById(
        showId = showId,
        language = language
    ).asFlow()

    override suspend fun getSimilarShowsById(
        showId: Int,
        language: String
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> = showService.getSimilarShowsById(
        showId = showId,
        language = language
    ).asFlow()

    override suspend fun getStreamingProviders(showId: Int): Flow<Either<WatchProvidersResponse, ApiError>> =
        showService.getStreamingProviders(
            showId = showId
        ).asFlow()
}
