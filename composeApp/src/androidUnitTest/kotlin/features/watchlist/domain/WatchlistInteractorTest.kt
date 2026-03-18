package features.watchlist.domain

import common.domain.models.util.MediaType
import common.util.errorFlow
import common.util.fakeContentEntity
import common.util.fakeListEntity
import common.util.fakeMovieResponse
import common.util.fakeShowResponse
import common.util.successFlow
import database.repository.DatabaseRepository
import database.repository.PersonalRatingRepository
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.DefaultLists
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import network.repository.movie.MovieRepository
import network.repository.show.ShowRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WatchlistInteractorTest {

    private val databaseRepository: DatabaseRepository = mockk()
    private val movieRepository: MovieRepository = mockk()
    private val showRepository: ShowRepository = mockk()
    private val personalRatingRepository: PersonalRatingRepository = mockk()

    private lateinit var interactor: WatchlistInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        interactor = WatchlistInteractor(
            databaseRepository = databaseRepository,
            movieRepository = movieRepository,
            showRepository = showRepository,
            personalRatingRepository = personalRatingRepository,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── getAllItems ────────────────────────────────────────────────────────────

    @Test
    fun `getAllItems returns list of entities for given listId`() = runTest {
        coEvery { databaseRepository.getAllItemsByListId(1) } returns listOf(
            fakeContentEntity(contentId = 1, listId = 1),
            fakeContentEntity(contentId = 2, listId = 1),
        )

        val result = interactor.getAllItems(1)

        assertEquals(2, result.size)
    }

    @Test
    fun `getAllItems returns empty list when no items exist`() = runTest {
        coEvery { databaseRepository.getAllItemsByListId(1) } returns emptyList()

        val result = interactor.getAllItems(1)

        assertTrue(result.isEmpty())
    }

    // ── fetchListDetails ──────────────────────────────────────────────────────

    @Test
    fun `fetchListDetails returns populated state for MOVIE entities`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name)
        coEvery { personalRatingRepository.getRating(1) } returns null
        coEvery { movieRepository.getMovieDetailsById(1) } returns successFlow(fakeMovieResponse(id = 1))

        val result = interactor.fetchListDetails(listOf(entity))

        assertFalse(result.isFailed())
        assertEquals(1, result.listItems.value.size)
        assertEquals(MediaType.MOVIE, result.listItems.value[0].mediaType)
    }

    @Test
    fun `fetchListDetails returns populated state for SHOW entities`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.SHOW.name)
        coEvery { personalRatingRepository.getRating(1) } returns null
        coEvery { showRepository.getShowDetailsById(1) } returns successFlow(fakeShowResponse(id = 1))

        val result = interactor.fetchListDetails(listOf(entity))

        assertFalse(result.isFailed())
        assertEquals(1, result.listItems.value.size)
        assertEquals(MediaType.SHOW, result.listItems.value[0].mediaType)
    }

    @Test
    fun `fetchListDetails skips entities with PERSON mediaType`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.PERSON.name)
        coEvery { personalRatingRepository.getRating(1) } returns null

        val result = interactor.fetchListDetails(listOf(entity))

        assertFalse(result.isFailed())
        assertTrue(result.listItems.value.isEmpty())
    }

    @Test
    fun `fetchListDetails attaches personalRating to each item`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name)
        coEvery { personalRatingRepository.getRating(1) } returns 8.5f
        coEvery { movieRepository.getMovieDetailsById(1) } returns successFlow(fakeMovieResponse(id = 1))

        val result = interactor.fetchListDetails(listOf(entity))

        assertEquals(8.5f, result.listItems.value[0].personalRating)
    }

    @Test
    fun `fetchListDetails returns error state when API returns error`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name)
        coEvery { personalRatingRepository.getRating(1) } returns null
        coEvery { movieRepository.getMovieDetailsById(1) } returns errorFlow("500")

        val result = interactor.fetchListDetails(listOf(entity))

        assertTrue(result.isFailed())
    }

    // ── removeContentFromDatabase ─────────────────────────────────────────────

    @Test
    fun `removeContentFromDatabase calls deleteItem with correct args`() = runTest {
        coEvery { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) } returns fakeContentEntity(1, 1)

        interactor.removeContentFromDatabase(contentId = 1, mediaType = MediaType.MOVIE, listId = 1)

        coVerify { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) }
    }

    // ── moveItemToList ────────────────────────────────────────────────────────

    @Test
    fun `moveItemToList calls databaseRepository moveItemToList with correct args`() = runTest {
        coEvery { databaseRepository.moveItemToList(1, MediaType.MOVIE, 1, 2) } returns fakeContentEntity(1, 2)

        interactor.moveItemToList(contentId = 1, mediaType = MediaType.MOVIE, currentListId = 1, newListId = 2)

        coVerify { databaseRepository.moveItemToList(1, MediaType.MOVIE, 1, 2) }
    }

    // ── undoItemRemoved ───────────────────────────────────────────────────────

    @Test
    fun `undoItemRemoved calls reinsertItem after removeContentFromDatabase`() = runTest {
        val entity = fakeContentEntity(1, 1)
        coEvery { databaseRepository.deleteItem(1, MediaType.MOVIE, 1) } returns entity
        coEvery { databaseRepository.reinsertItem(entity) } returns Unit

        interactor.removeContentFromDatabase(1, MediaType.MOVIE, 1)
        interactor.undoItemRemoved()

        coVerify { databaseRepository.reinsertItem(entity) }
    }

    @Test
    fun `undoItemRemoved does nothing when no item has been removed`() = runTest {
        interactor.undoItemRemoved()

        coVerify(exactly = 0) { databaseRepository.reinsertItem(any()) }
    }

    // ── undoMovedItem ─────────────────────────────────────────────────────────

    @Test
    fun `undoMovedItem calls reinsertItem and deleteItem after moveItemToList`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1)
        coEvery { databaseRepository.moveItemToList(1, MediaType.MOVIE, 1, 2) } returns entity
        coEvery { databaseRepository.reinsertItem(entity) } returns Unit
        coEvery { databaseRepository.deleteItem(1, MediaType.MOVIE, 2) } returns null

        interactor.moveItemToList(1, MediaType.MOVIE, 1, 2)
        interactor.undoMovedItem()

        coVerify { databaseRepository.reinsertItem(entity) }
        coVerify { databaseRepository.deleteItem(1, MediaType.MOVIE, 2) }
    }

    @Test
    fun `undoMovedItem does nothing when no item has been moved`() = runTest {
        interactor.undoMovedItem()

        coVerify(exactly = 0) { databaseRepository.reinsertItem(any()) }
    }

    // ── getAllLists ───────────────────────────────────────────────────────────

    @Test
    fun `getAllLists maps WATCHLIST listId to WatchlistTab`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(DefaultLists.WATCHLIST.listId, "Watchlist"),
        )

        val result = interactor.getAllLists()

        assertTrue(result.any { it is WatchlistTabItem.WatchlistTab })
    }

    @Test
    fun `getAllLists maps WATCHED listId to WatchedTab`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(DefaultLists.WATCHED.listId, "Watched"),
        )

        val result = interactor.getAllLists()

        assertTrue(result.any { it is WatchlistTabItem.WatchedTab })
    }

    @Test
    fun `getAllLists maps other listId to CustomTab with correct name and id`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(99, "My Favs"),
        )

        val result = interactor.getAllLists()

        val customTab = result.filterIsInstance<WatchlistTabItem.CustomTab>().firstOrNull()
        assertNotNull(customTab)
        assertEquals("My Favs", customTab.tabName)
        assertEquals(99, customTab.listId)
    }

    @Test
    fun `getAllLists appends AddNewTab when list count is below maximum`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(DefaultLists.WATCHLIST.listId),
            fakeListEntity(DefaultLists.WATCHED.listId),
        )

        val result = interactor.getAllLists()

        assertTrue(result.last() is WatchlistTabItem.AddNewTab)
    }

    @Test
    fun `getAllLists does not append AddNewTab when list count equals maximum`() = runTest {
        // MAX_WATCHLIST_LIST_NUMBER = 12
        coEvery { databaseRepository.getAllLists() } returns List(12) { fakeListEntity(it + 10, "List ${it + 10}") }

        val result = interactor.getAllLists()

        assertFalse(result.any { it is WatchlistTabItem.AddNewTab })
    }

    @Test
    fun `getAllLists assigns ascending tabIndex to all items`() = runTest {
        coEvery { databaseRepository.getAllLists() } returns listOf(
            fakeListEntity(DefaultLists.WATCHLIST.listId, "Watchlist"),
            fakeListEntity(DefaultLists.WATCHED.listId, "Watched"),
        )

        val result = interactor.getAllLists()

        result.forEachIndexed { index, item ->
            assertEquals(index, item.tabIndex)
        }
    }

    // ── deleteList ────────────────────────────────────────────────────────────

    @Test
    fun `deleteList delegates to databaseRepository deleteList`() = runTest {
        coEvery { databaseRepository.deleteList(5) } returns Unit

        interactor.deleteList(5)

        coVerify { databaseRepository.deleteList(5) }
    }
}
