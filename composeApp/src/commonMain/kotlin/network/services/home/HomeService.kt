package network.services.home

import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult

interface HomeService {
    suspend fun getDayTrendingMulti(
        language: String = "en-US",
    ): ApiResult<ContentPagingResponse<MultiResponse>>

    suspend fun getDayTrendingPerson(
        language: String = "en-US",
    ): ApiResult<ContentPagingResponse<PersonResponse>>

    suspend fun getMoviesComingSoon(
        language: String = "en-US",
        releaseDateGte: String,
        releaseDateLte: String,
    ): ApiResult<ContentPagingResponse<MovieResponse>>
}
