package network.repository.person

import kotlinx.coroutines.flow.Flow
import network.models.ApiError
import network.models.content.common.PersonResponse
import network.models.content.person.PersonCreditsResponse
import network.models.content.person.PersonImagesResponse
import network.services.person.PersonService
import network.util.Either
import network.util.asFlow

class PersonRepositoryImpl(
    private val personService: PersonService,
) : PersonRepository {
    override suspend fun getPersonDetailsById(
        personId: Int,
    ): Flow<Either<PersonResponse, ApiError>> {
        return personService.getPersonDetailsById(personId).asFlow()
    }

    override suspend fun getPersonCreditsById(
        personId: Int,
    ): Flow<Either<PersonCreditsResponse, ApiError>> {
        return personService.getPersonCreditsById(personId).asFlow()
    }

    override suspend fun getPersonImagesById(
        personId: Int,
    ): Flow<Either<PersonImagesResponse, ApiError>> {
        return personService.getPersonImagesById(personId).asFlow()
    }
}
