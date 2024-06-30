package network.repository.person

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.PersonResponse
import network.models.content.person.PersonCreditsResponse
import network.models.content.person.PersonImagesResponse
import network.util.Either

interface PersonRepository {
    suspend fun getPersonDetailsById(
        personId: Int,
    ): Flow<Either<PersonResponse, ApiError>>

    suspend fun getPersonCreditsById(
        personId: Int,
    ): Flow<Either<PersonCreditsResponse, ApiError>>

    suspend fun getPersonImagesById(
        personId: Int,
    ): Flow<Either<PersonImagesResponse, ApiError>>
}
