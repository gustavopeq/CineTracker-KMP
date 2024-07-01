package network.repository.search

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse
import network.models.content.search.ContentPagingResponse
import network.services.search.SearchService
import network.util.Either
import network.util.asFlow

class SearchRepositoryImpl(
    private val searchService: SearchService,
) : SearchRepository {
    override suspend fun onSearchMultiByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<MultiResponse>, ApiError>> {
        return searchService.searchMultiByQuery(
            query = query,
            pageIndex = page,
        ).asFlow()
    }

    override suspend fun onSearchMovieByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>> {
        return searchService.searchMovieByQuery(
            query = query,
            pageIndex = page,
        ).asFlow()
    }

    override suspend fun onSearchShowByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>> {
        return searchService.searchShowByQuery(
            query = query,
            pageIndex = page,
        ).asFlow()
    }

    override suspend fun onSearchPersonByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<PersonResponse>, ApiError>> {
        return searchService.searchPersonByQuery(
            query = query,
            pageIndex = page,
        ).asFlow()
    }
}
