package network.repository.person

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.PersonResponse
import network.models.content.person.PersonCreditsResponse
import network.models.content.person.PersonImagesResponse
import network.services.person.PersonService
import network.util.Either
import network.util.asFlow

class PersonRepositoryImpl(private val personService: PersonService) : PersonRepository {
    override suspend fun getPersonDetailsById(
        personId: Int,
        language: String
    ): Flow<Either<PersonResponse, ApiError>> =
        personService.getPersonDetailsById(personId, language = language).asFlow()

    override suspend fun getPersonCreditsById(
        personId: Int,
        language: String
    ): Flow<Either<PersonCreditsResponse, ApiError>> =
        personService.getPersonCreditsById(personId, language = language).asFlow()

    override suspend fun getPersonImagesById(personId: Int): Flow<Either<PersonImagesResponse, ApiError>> =
        personService.getPersonImagesById(personId).asFlow()
}
