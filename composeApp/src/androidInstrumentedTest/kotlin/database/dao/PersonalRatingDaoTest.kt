package database.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import database.AppDatabase
import database.model.PersonalRatingEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonalRatingDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PersonalRatingDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.personalRatingDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ── getRating ─────────────────────────────────────────────────────────────

    @Test
    fun getRating_returnsEntityWhenFound() = runBlocking {
        dao.insertRating(PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 8.5f))

        val result = dao.getRating(1)

        assertNotNull(result)
        assertEquals(8.5f, result!!.rating)
        assertEquals("MOVIE", result.mediaType)
    }

    @Test
    fun getRating_returnsNullWhenNotFound() = runBlocking {
        val result = dao.getRating(99)

        assertNull(result)
    }

    // ── insertRating ──────────────────────────────────────────────────────────

    @Test
    fun insertRating_replacesExistingRatingForSameContentId() = runBlocking {
        dao.insertRating(PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 6.0f))
        dao.insertRating(PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 9.0f))

        val result = dao.getRating(1)

        assertNotNull(result)
        assertEquals(9.0f, result!!.rating)
        assertEquals(1, dao.getAllRatings().size) // only one row, not two
    }

    // ── deleteRating ──────────────────────────────────────────────────────────

    @Test
    fun deleteRating_removesExistingRating() = runBlocking {
        dao.insertRating(PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 7.0f))

        dao.deleteRating(1)

        assertNull(dao.getRating(1))
    }

    @Test
    fun deleteRating_doesNothingWhenRatingDoesNotExist() = runBlocking {
        dao.deleteRating(99)

        assertEquals(0, dao.getAllRatings().size)
    }

    @Test
    fun deleteRating_doesNotAffectOtherRatings() = runBlocking {
        dao.insertRating(PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 7.0f))
        dao.insertRating(PersonalRatingEntity(contentId = 2, mediaType = "SHOW", rating = 5.0f))

        dao.deleteRating(1)

        assertNull(dao.getRating(1))
        assertNotNull(dao.getRating(2))
    }

    // ── getAllRatings ─────────────────────────────────────────────────────────

    @Test
    fun getAllRatings_returnsAllInsertedRatings() = runBlocking {
        dao.insertRating(PersonalRatingEntity(contentId = 1, mediaType = "MOVIE", rating = 7.0f))
        dao.insertRating(PersonalRatingEntity(contentId = 2, mediaType = "SHOW", rating = 5.0f))
        dao.insertRating(PersonalRatingEntity(contentId = 3, mediaType = "MOVIE", rating = 9.0f))

        val result = dao.getAllRatings()

        assertEquals(3, result.size)
    }

    @Test
    fun getAllRatings_returnsEmptyWhenNoRatingsExist() = runBlocking {
        val result = dao.getAllRatings()

        assertEquals(0, result.size)
    }
}
