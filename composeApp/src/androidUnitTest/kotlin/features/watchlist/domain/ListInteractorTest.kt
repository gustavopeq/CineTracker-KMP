package features.watchlist.domain

import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import common.util.fakeListEntity
import database.repository.DatabaseRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ListInteractorTest {

    private val databaseRepository: DatabaseRepository = mockk()

    private lateinit var interactor: ListInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        interactor = ListInteractor(databaseRepository = databaseRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── getAllLists ───────────────────────────────────────────────────────────

    @Test
    fun `getAllLists maps ListEntity to ListItem`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(listId = 1, name = "Watchlist", isDefault = true),
                fakeListEntity(listId = 2, name = "Watched", isDefault = true)
            )
        )

        val result = interactor.getAllLists().first()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Watchlist", result[0].name)
        assertTrue(result[0].isDefault)
        assertEquals(2, result[1].id)
        assertEquals("Watched", result[1].name)
    }

    @Test
    fun `getAllLists returns empty list when no lists exist`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(emptyList())

        val result = interactor.getAllLists().first()

        assertTrue(result.isEmpty())
    }

    // ── verifyContentInLists ─────────────────────────────────────────────────

    @Test
    fun `verifyContentInLists returns true for lists containing the content`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(listId = 1, name = "Watchlist"),
                fakeListEntity(listId = 2, name = "Watched")
            )
        )
        every { databaseRepository.searchItems(10, MediaType.MOVIE) } returns flowOf(
            listOf(
                fakeContentEntity(contentId = 10, listId = 1)
            )
        )

        val result = interactor.verifyContentInLists(10, MediaType.MOVIE).first()

        assertTrue(result[1] == true)
        assertFalse(result[2] == true)
    }

    @Test
    fun `verifyContentInLists returns all false when content not in any list`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(listId = 1, name = "Watchlist"),
                fakeListEntity(listId = 2, name = "Watched")
            )
        )
        every { databaseRepository.searchItems(10, MediaType.MOVIE) } returns flowOf(emptyList())

        val result = interactor.verifyContentInLists(10, MediaType.MOVIE).first()

        assertFalse(result[1] == true)
        assertFalse(result[2] == true)
    }

    @Test
    fun `verifyContentInLists marks multiple lists when content in several`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(listId = 1, name = "Watchlist"),
                fakeListEntity(listId = 2, name = "Watched"),
                fakeListEntity(listId = 3, name = "Favorites")
            )
        )
        every { databaseRepository.searchItems(5, MediaType.SHOW) } returns flowOf(
            listOf(
                fakeContentEntity(contentId = 5, listId = 1, mediaType = MediaType.SHOW.name),
                fakeContentEntity(contentId = 5, listId = 3, mediaType = MediaType.SHOW.name)
            )
        )

        val result = interactor.verifyContentInLists(5, MediaType.SHOW).first()

        assertEquals(3, result.size)
        assertTrue(result[1] == true)
        assertFalse(result[2] == true)
        assertTrue(result[3] == true)
    }

    // ── toggleWatchlist ──────────────────────────────────────────────────────

    @Test
    fun `toggleWatchlist calls deleteItem when currentStatus is true`() = runTest {
        coEvery { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) } returns fakeContentEntity()

        interactor.toggleWatchlist(
            currentStatus = true,
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1
        )

        coVerify { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) }
        coVerify(exactly = 0) { databaseRepository.insertItem(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `toggleWatchlist calls insertItem when currentStatus is false`() = runTest {
        coEvery { databaseRepository.insertItem(1, MediaType.MOVIE, 1, any(), any(), any()) } returns Unit

        interactor.toggleWatchlist(
            currentStatus = false,
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1
        )

        coVerify { databaseRepository.insertItem(1, MediaType.MOVIE, 1, any(), any(), any()) }
        coVerify(exactly = 0) { databaseRepository.deleteItem(any(), any(), any()) }
    }

    @Test
    fun `toggleWatchlist passes cached fields to insertItem when adding`() = runTest {
        coEvery { databaseRepository.insertItem(any(), any(), any(), any(), any(), any()) } returns Unit

        interactor.toggleWatchlist(
            currentStatus = false,
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1,
            title = "Cached Title",
            posterPath = "/cached.jpg",
            voteAverage = 8.5f
        )

        coVerify {
            databaseRepository.insertItem(
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = 1,
                title = "Cached Title",
                posterPath = "/cached.jpg",
                voteAverage = 8.5f
            )
        }
    }

    @Test
    fun `toggleWatchlist does not pass cached fields to deleteItem when removing`() = runTest {
        coEvery { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) } returns fakeContentEntity()

        interactor.toggleWatchlist(
            currentStatus = true,
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1,
            title = "Ignored Title",
            posterPath = "/ignored.jpg",
            voteAverage = 9.0f
        )

        coVerify { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) }
        coVerify(exactly = 0) { databaseRepository.insertItem(any(), any(), any(), any(), any(), any()) }
    }
}
