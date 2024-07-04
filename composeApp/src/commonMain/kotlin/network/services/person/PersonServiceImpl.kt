package network.services.person

import com.projects.moviemanager.network.util.Parameters
import io.ktor.client.HttpClient
import network.models.content.common.PersonResponse
import network.models.content.person.PersonCreditsResponse
import network.models.content.person.PersonImagesResponse
import network.util.ApiResult
import network.util.buildUrl
import network.util.getResult

class PersonServiceImpl(
    private val client: HttpClient,
) : PersonService {
    override suspend fun getPersonDetailsById(
        personId: Int,
        language: String,
    ): ApiResult<PersonResponse> {
        val path = "person/$personId"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getPersonCreditsById(
        personId: Int,
        language: String,
    ): ApiResult<PersonCreditsResponse> {
        val path = "person/$personId/combined_credits"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }

    override suspend fun getPersonImagesById(
        personId: Int,
        language: String,
    ): ApiResult<PersonImagesResponse> {
        val path = "person/$personId/images"
        val url = buildUrl(path) {
            mapOf(
                Parameters.LANGUAGE to language,
            )
        }

        return client.getResult(url)
    }
}
