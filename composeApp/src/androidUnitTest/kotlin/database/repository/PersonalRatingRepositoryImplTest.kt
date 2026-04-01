package database.repository

import common.domain.models.util.MediaType
import database.dao.PersonalRatingDao
import database.model.PersonalRatingEntity
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
    fun `setRating stores SHOW mediaType string for SHOW content`() = runTest {
        repository.setRating(2, MediaType.SHOW, 6.0f)

        coVerify {
            personalRatingDao.insertRating(
                match { it.contentId == 2 && it.rating == 6.0f && it.mediaType == "SHOW" }
            )
        }
    }

    @Test
    fun `getRating returns rating float from entity`() = runTest {
        every { personalRatingDao.getRating(1) } returns flowOf(
            PersonalRatingEntity(
                contentId = 1,
                mediaType = "MOVIE",
                rating = 7.5f
            )
        )

        val result = repository.getRating(1).first()

        assertEquals(7.5f, result)
    }

    @Test
    fun `getRating returns null when entity not found`() = runTest {
        every { personalRatingDao.getRating(any()) } returns flowOf(null)

        val result = repository.getRating(99).first()

        assertNull(result)
    }

    @Test
    fun `getAllRatings returns map of contentId to rating`() = runTest {
        every { personalRatingDao.getAllRatings() } returns flowOf(
            listOf(
                PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 7.5f),
                PersonalRatingEntity(contentId = 2, mediaType = "SHOW", rating = 9.0f)
            )
        )

        val result = repository.getAllRatings().first()

        assertEquals(2, result.size)
        assertEquals(7.5f, result[1])
        assertEquals(9.0f, result[2])
    }

    @Test
    fun `getAllRatings returns empty map when no ratings exist`() = runTest {
        every { personalRatingDao.getAllRatings() } returns flowOf(emptyList())

        val result = repository.getAllRatings().first()

        assertTrue(result.isEmpty())
    }
}
