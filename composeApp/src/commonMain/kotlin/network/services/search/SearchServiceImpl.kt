package network.services.search

import com.projects.moviemanager.network.util.Parameters.LANGUAGE
import com.projects.moviemanager.network.util.Parameters.MATURE_ENABLED
import com.projects.moviemanager.network.util.Parameters.PAGE_INDEX
import com.projects.moviemanager.network.util.Parameters.SEARCH_QUERY
import io.ktor.client.HttpClient
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.common.ShowResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult
import network.util.buildUrl
import network.util.getResult

class SearchServiceImpl(
    private val client: HttpClient,
) : SearchService {

    companion object {
        const val SEARCH_MULTI = "search/multi"
        const val SEARCH_MOVIE = "search/movie"
        const val SEARCH_TV = "search/tv"
        const val SEARCH_PERSON = "search/person"
    }

    override suspend fun searchMultiByQuery(
        query: String,
        matureEnabled: Boolean,
        language: String,
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<MultiResponse>> {
        val url = buildUrl(SEARCH_MULTI) {
            mapOf(
                SEARCH_QUERY to query,
                MATURE_ENABLED to matureEnabled.toString(),
                LANGUAGE to language,
                PAGE_INDEX to pageIndex.toString(),
            )
        }

        return client.getResult(url)
    }

    override suspend fun searchMovieByQuery(
        query: String,
        matureEnabled: Boolean,
        language: String,
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<MovieResponse>> {
        val url = buildUrl(SEARCH_MOVIE) {
            mapOf(
                SEARCH_QUERY to query,
                MATURE_ENABLED to matureEnabled.toString(),
                LANGUAGE to language,
                PAGE_INDEX to pageIndex.toString(),
            )
        }

        return client.getResult(url)
    }

    override suspend fun searchShowByQuery(
        query: String,
        matureEnabled: Boolean,
        language: String,
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<ShowResponse>> {
        val url = buildUrl(SEARCH_TV) {
            mapOf(
                SEARCH_QUERY to query,
                MATURE_ENABLED to matureEnabled.toString(),
                LANGUAGE to language,
                PAGE_INDEX to pageIndex.toString(),
            )
        }

        return client.getResult(url)
    }

    override suspend fun searchPersonByQuery(
        query: String,
        matureEnabled: Boolean,
        language: String,
        pageIndex: Int,
    ): ApiResult<ContentPagingResponse<PersonResponse>> {
        val url = buildUrl(SEARCH_PERSON) {
            mapOf(
                SEARCH_QUERY to query,
                MATURE_ENABLED to matureEnabled.toString(),
                LANGUAGE to language,
                PAGE_INDEX to pageIndex.toString(),
            )
        }

        return client.getResult(url)
    }
}
