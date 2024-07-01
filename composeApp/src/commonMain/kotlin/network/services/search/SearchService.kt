package network.services.search

import core.LanguageManager
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult

interface SearchService {
    suspend fun searchMultiByQuery(
        query: String,
        matureEnabled: Boolean = false,
        language: String = LanguageManager.getUserLanguageTag(),
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<MultiResponse>>

    suspend fun searchMovieByQuery(
        query: String,
        matureEnabled: Boolean = false,
        language: String = LanguageManager.getUserLanguageTag(),
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<MovieResponse>>

    suspend fun searchShowByQuery(
        query: String,
        matureEnabled: Boolean = false,
        language: String = LanguageManager.getUserLanguageTag(),
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<ShowResponse>>

    suspend fun searchPersonByQuery(
        query: String,
        matureEnabled: Boolean = false,
        language: String = LanguageManager.getUserLanguageTag(),
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<PersonResponse>>
}
