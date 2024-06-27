package network.services.home

import com.projects.moviemanager.network.util.Parameters.LANGUAGE
import com.projects.moviemanager.network.util.Parameters.RELEASE_DATE_GTE
import com.projects.moviemanager.network.util.Parameters.RELEASE_DATE_LTE
import io.ktor.client.HttpClient
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult
import network.util.buildUrl
import network.util.getResult

class HomeServiceImpl(
    private val client: HttpClient,
) : HomeService {

    companion object {
        const val MULTI_DAY_TRENDING = "/trending/all/day"
        const val PERSON_DAY_TRENDING = "/trending/person/day"
        const val MOVIES_COMING_SOON = "/discover/movie"
    }

    override suspend fun getDayTrendingMulti(
        language: String,
    ): ApiResult<ContentPagingResponse<MultiResponse>> {
        val url = buildUrl(MULTI_DAY_TRENDING) {
            mapOf(
                LANGUAGE to language,
            )
        }
        return client.getResult(url)
    }

    override suspend fun getDayTrendingPerson(
        language: String,
    ): ApiResult<ContentPagingResponse<PersonResponse>> {
        val url = buildUrl(PERSON_DAY_TRENDING) {
            mapOf(
                LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getMoviesComingSoon(
        language: String,
        releaseDateGte: String,
        releaseDateLte: String,
    ): ApiResult<ContentPagingResponse<MovieResponse>> {
        val url = buildUrl(MOVIES_COMING_SOON) {
            mapOf(
                LANGUAGE to language,
                RELEASE_DATE_GTE to releaseDateGte,
                RELEASE_DATE_LTE to releaseDateLte,
            )
        }

        return client.getResult(url)
    }
}
