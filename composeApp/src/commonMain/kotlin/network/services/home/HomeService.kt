package network.services.home

import core.LanguageManager.getUserLanguageTag
import network.models.content.common.MovieResponse
import network.models.content.common.MultiResponse
import network.models.content.common.PersonResponse
import network.models.content.search.ContentPagingResponse
import network.util.ApiResult

interface HomeService {
    suspend fun getDayTrendingMulti(
        language: String = getUserLanguageTag(),
    ): ApiResult<ContentPagingResponse<MultiResponse>>

    suspend fun getDayTrendingPerson(
        language: String = getUserLanguageTag(),
    ): ApiResult<ContentPagingResponse<PersonResponse>>

    suspend fun getMoviesComingSoon(
        language: String = getUserLanguageTag(),
        releaseDateGte: String,
        releaseDateLte: String,
    ): ApiResult<ContentPagingResponse<MovieResponse>>
}
