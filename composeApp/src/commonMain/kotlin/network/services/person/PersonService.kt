package network.services.person

import network.models.content.common.PersonResponse
import network.models.content.person.PersonCreditsResponse
import network.models.content.person.PersonImagesResponse
import network.util.ApiResult

interface PersonService {
    suspend fun getPersonDetailsById(personId: Int, language: String): ApiResult<PersonResponse>

    suspend fun getPersonCreditsById(personId: Int, language: String): ApiResult<PersonCreditsResponse>

    suspend fun getPersonImagesById(personId: Int): ApiResult<PersonImagesResponse>
}
