package database.repository

import common.domain.models.util.MediaType
import database.dao.PersonalRatingDao
import database.model.PersonalRatingEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class PersonalRatingRepositoryImplTest {

    private val personalRatingDao: PersonalRatingDao = mockk(relaxUnitFun = true)

    private lateinit var repository: PersonalRatingRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = PersonalRatingRepositoryImpl(personalRatingDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `setRating inserts entity when rating is non-null`() = runTest {
        repository.setRating(1, MediaType.MOVIE, 8.5f)

        coVerify {
            personalRatingDao.insertRating(
                match { it.contentId == 1 && it.rating == 8.5f && it.mediaType == "MOVIE" }
            )
        }
        coVerify(exactly = 0) { personalRatingDao.deleteRating(any()) }
    }

    @Test
    fun `setRating deletes entity when rating is null`() = runTest {
        repository.setRating(1, MediaType.MOVIE, null)

        coVerify { personalRatingDao.deleteRating(1) }
        coVerify(exactly = 0) { personalRatingDao.insertRating(any()) }
    }

    @Test
    fun `getRating returns rating float from entity`() = runTest {
        coEvery { personalRatingDao.getRating(1) } returns PersonalRatingEntity(
            contentId = 1,
            mediaType = "MOVIE",
            rating = 7.5f
        )

        val result = repository.getRating(1)

        assertEquals(7.5f, result)
    }

    @Test
    fun `getRating returns null when entity not found`() = runTest {
        coEvery { personalRatingDao.getRating(any()) } returns null

        val result = repository.getRating(99)

        assertNull(result)
    }
}
