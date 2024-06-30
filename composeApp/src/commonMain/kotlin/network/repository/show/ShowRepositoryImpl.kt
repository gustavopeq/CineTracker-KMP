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

class ShowRepositoryImpl(
    private val showService: ShowService,
) : ShowRepository {
    override suspend fun getShowList(
        contentListType: String,
        pageIndex: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> {
        return showService.getShowList(
            contentListType = contentListType,
            pageIndex = pageIndex,
            language = "en-US",
        ).asFlow()
    }

    override suspend fun getShowDetailsById(showId: Int): Flow<Either<ShowResponse, ApiError>> {
        return showService.getShowDetailsById(
            showId = showId,
        ).asFlow()
    }

    override suspend fun getShowCreditsById(
        showId: Int,
    ): Flow<Either<ContentCreditsResponse, ApiError>> {
        return showService.getShowCreditsById(
            showId = showId,
        ).asFlow()
    }

    override suspend fun getShowVideosById(
        showId: Int,
    ): Flow<Either<VideosByIdResponse, ApiError>> {
        return showService.getShowVideosById(
            showId = showId,
        ).asFlow()
    }

    override suspend fun getRecommendationsShowsById(
        showId: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> {
        return showService.getRecommendationsShowsById(
            showId = showId,
        ).asFlow()
    }

    override suspend fun getSimilarShowsById(
        showId: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> {
        return showService.getSimilarShowsById(
            showId = showId,
        ).asFlow()
    }

    override suspend fun getStreamingProviders(
        showId: Int,
    ): Flow<Either<WatchProvidersResponse, ApiError>> {
        return showService.getStreamingProviders(
            showId = showId,
        ).asFlow()
    }
}
