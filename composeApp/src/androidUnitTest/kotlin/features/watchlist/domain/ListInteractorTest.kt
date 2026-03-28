package features.watchlist.domain

import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import common.util.fakeListEntity
import database.repository.DatabaseRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
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
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(listId = 1, name = "Watchlist", isDefault = true),
            fakeListEntity(listId = 2, name = "Watched", isDefault = true)
        )

        val result = interactor.getAllLists()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Watchlist", result[0].name)
        assertTrue(result[0].isDefault)
        assertEquals(2, result[1].id)
        assertEquals("Watched", result[1].name)
    }

    @Test
    fun `getAllLists returns empty list when no lists exist`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns emptyList()

        val result = interactor.getAllLists()

        assertTrue(result.isEmpty())
    }

    // ── verifyContentInLists ─────────────────────────────────────────────────

    @Test
    fun `verifyContentInLists returns true for lists containing the content`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(listId = 1, name = "Watchlist"),
            fakeListEntity(listId = 2, name = "Watched")
        )
        coEvery { databaseRepository.searchItems(10, MediaType.MOVIE) } returns listOf(
            fakeContentEntity(contentId = 10, listId = 1)
        )

        val result = interactor.verifyContentInLists(10, MediaType.MOVIE)

        assertTrue(result[1] == true)
        assertFalse(result[2] == true)
    }

    @Test
    fun `verifyContentInLists returns all false when content not in any list`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(listId = 1, name = "Watchlist"),
            fakeListEntity(listId = 2, name = "Watched")
        )
        coEvery { databaseRepository.searchItems(10, MediaType.MOVIE) } returns emptyList()

        val result = interactor.verifyContentInLists(10, MediaType.MOVIE)

        assertFalse(result[1] == true)
        assertFalse(result[2] == true)
    }

    @Test
    fun `verifyContentInLists marks multiple lists when content in several`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(listId = 1, name = "Watchlist"),
            fakeListEntity(listId = 2, name = "Watched"),
            fakeListEntity(listId = 3, name = "Favorites")
        )
        coEvery { databaseRepository.searchItems(5, MediaType.SHOW) } returns listOf(
            fakeContentEntity(contentId = 5, listId = 1, mediaType = MediaType.SHOW.name),
            fakeContentEntity(contentId = 5, listId = 3, mediaType = MediaType.SHOW.name)
        )

        val result = interactor.verifyContentInLists(5, MediaType.SHOW)

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
        coVerify(exactly = 0) { databaseRepository.insertItem(any(), any(), any()) }
    }

    @Test
    fun `toggleWatchlist calls insertItem when currentStatus is false`() = runTest {
        coEvery { databaseRepository.insertItem(1, MediaType.MOVIE, 1) } returns Unit

        interactor.toggleWatchlist(
            currentStatus = false,
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1
        )

        coVerify { databaseRepository.insertItem(1, MediaType.MOVIE, 1) }
        coVerify(exactly = 0) { databaseRepository.deleteItem(any(), any(), any()) }
    }
}
