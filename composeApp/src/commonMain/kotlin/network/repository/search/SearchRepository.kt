package network.repository.search

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse
import network.models.content.search.ContentPagingResponse
import network.util.Either

interface SearchRepository {
    suspend fun onSearchMultiByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<MultiResponse>, ApiError>>

    suspend fun onSearchMovieByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<MovieResponse>, ApiError>>

    suspend fun onSearchShowByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<ShowResponse>, ApiError>>

    suspend fun onSearchPersonByQuery(
        query: String,
        page: Int,
    ): Flow<Either<ContentPagingResponse<PersonResponse>, ApiError>>
}
