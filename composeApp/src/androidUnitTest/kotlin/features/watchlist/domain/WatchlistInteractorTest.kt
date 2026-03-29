package features.watchlist.domain

import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import common.util.fakeListEntity
import database.repository.DatabaseRepository
import database.repository.PersonalRatingRepository
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.DefaultLists
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class WatchlistInteractorTest {

    private val databaseRepository: DatabaseRepository = mockk()
    private val personalRatingRepository: PersonalRatingRepository = mockk()

    private lateinit var interactor: WatchlistInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        interactor = WatchlistInteractor(
            databaseRepository = databaseRepository,
            personalRatingRepository = personalRatingRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── getListContentWithRatings ─────────────────────────────────────────────

    @Test
    fun `getListContentWithRatings returns list of GenericContent for given listId`() = runTest {
        every { databaseRepository.getAllItemsByListId(1) } returns flowOf(
            listOf(
                fakeContentEntity(contentId = 1, listId = 1),
                fakeContentEntity(contentId = 2, listId = 1)
            )
        )
        every { personalRatingRepository.getAllRatings() } returns flowOf(emptyMap())

        val result = interactor.getListContentWithRatings(1).first()

        assertEquals(2, result.size)
    }

    @Test
    fun `getListContentWithRatings returns empty list when no items exist`() = runTest {
        every { databaseRepository.getAllItemsByListId(1) } returns flowOf(emptyList())
        every { personalRatingRepository.getAllRatings() } returns flowOf(emptyMap())

        val result = interactor.getListContentWithRatings(1).first()

        assertTrue(result.isEmpty())
    }

    // ── mapEntitiesToGenericContent ───────────────────────────────────────────

    @Test
    fun `mapEntitiesToGenericContent maps entities to GenericContent for MOVIE`() {
        val entities = listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name)
        )

        val result = interactor.mapEntitiesToGenericContent(entities)

        assertEquals(1, result.size)
        assertEquals(MediaType.MOVIE, result[0].mediaType)
    }

    @Test
    fun `mapEntitiesToGenericContent maps entities to GenericContent for SHOW`() {
        val entities = listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.SHOW.name)
        )

        val result = interactor.mapEntitiesToGenericContent(entities)

        assertEquals(1, result.size)
        assertEquals(MediaType.SHOW, result[0].mediaType)
    }

    @Test
    fun `mapEntitiesToGenericContent includes entities with null posterPath`() {
        val entities = listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name),
            fakeContentEntity(contentId = 2, listId = 1, mediaType = MediaType.MOVIE.name, posterPath = null)
        )

        val result = interactor.mapEntitiesToGenericContent(entities)

        assertEquals(2, result.size)
        assertEquals("", result[1].posterPath)
    }

    @Test
    fun `mapEntitiesToGenericContent returns empty list for empty input`() {
        val result = interactor.mapEntitiesToGenericContent(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `mapEntitiesToGenericContent maps cached fields correctly`() {
        val entities = listOf(
            fakeContentEntity(
                contentId = 1,
                listId = 1,
                mediaType = MediaType.MOVIE.name,
                title = "Cached Title",
                posterPath = "/cached_poster.jpg",
                voteAverage = 8.5f
            )
        )

        val result = interactor.mapEntitiesToGenericContent(entities)

        assertEquals(1, result.size)
        assertEquals("Cached Title", result[0].name)
        assertEquals("/cached_poster.jpg", result[0].posterPath)
        assertEquals(8.5, result[0].rating, 0.01)
    }

    @Test
    fun `mapEntitiesToGenericContent preserves mediaType from entity`() {
        val entities = listOf(
            fakeContentEntity(contentId = 1, listId = 1, mediaType = MediaType.MOVIE.name),
            fakeContentEntity(contentId = 2, listId = 1, mediaType = MediaType.SHOW.name)
        )

        val result = interactor.mapEntitiesToGenericContent(entities)

        assertEquals(2, result.size)
        assertEquals(MediaType.MOVIE, result[0].mediaType)
        assertEquals(MediaType.SHOW, result[1].mediaType)
    }

    @Test
    fun `mapEntitiesToGenericContent attaches personalRating from ratingsMap`() {
        val entities = listOf(
            fakeContentEntity(contentId = 1, posterPath = "/poster.jpg"),
            fakeContentEntity(contentId = 2, posterPath = "/poster2.jpg")
        )
        val ratingsMap = mapOf(1 to 8.5f)

        val result = interactor.mapEntitiesToGenericContent(entities, ratingsMap)

        assertEquals(8.5f, result[0].personalRating)
        assertNull(result[1].personalRating)
    }

    @Test
    fun `mapEntitiesToGenericContent uses null personalRating when ratingsMap is empty`() {
        val entities = listOf(fakeContentEntity(contentId = 1, posterPath = "/poster.jpg"))

        val result = interactor.mapEntitiesToGenericContent(entities)

        assertNull(result[0].personalRating)
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
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(DefaultLists.WATCHLIST.listId, "Watchlist")
            )
        )

        val result = interactor.getAllLists().first()

        assertTrue(result.any { it is WatchlistTabItem.WatchlistTab })
    }

    @Test
    fun `getAllLists maps WATCHED listId to WatchedTab`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(DefaultLists.WATCHED.listId, "Watched")
            )
        )

        val result = interactor.getAllLists().first()

        assertTrue(result.any { it is WatchlistTabItem.WatchedTab })
    }

    @Test
    fun `getAllLists maps other listId to CustomTab with correct name and id`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(99, "My Favs")
            )
        )

        val result = interactor.getAllLists().first()

        val customTab = result.filterIsInstance<WatchlistTabItem.CustomTab>().firstOrNull()
        assertNotNull(customTab)
        assertEquals("My Favs", customTab.tabName)
        assertEquals(99, customTab.listId)
    }

    @Test
    fun `getAllLists appends AddNewTab when list count is below maximum`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(DefaultLists.WATCHLIST.listId),
                fakeListEntity(DefaultLists.WATCHED.listId)
            )
        )

        val result = interactor.getAllLists().first()

        assertTrue(result.last() is WatchlistTabItem.AddNewTab)
    }

    @Test
    fun `getAllLists does not append AddNewTab when list count equals maximum`() = runTest {
        // MAX_WATCHLIST_LIST_NUMBER = 12
        every { databaseRepository.getAllLists() } returns flowOf(
            List(12) { fakeListEntity(it + 10, "List ${it + 10}") }
        )

        val result = interactor.getAllLists().first()

        assertFalse(result.any { it is WatchlistTabItem.AddNewTab })
    }

    @Test
    fun `getAllLists assigns ascending tabIndex to all items`() = runTest {
        every { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                fakeListEntity(DefaultLists.WATCHLIST.listId, "Watchlist"),
                fakeListEntity(DefaultLists.WATCHED.listId, "Watched")
            )
        )

        val result = interactor.getAllLists().first()

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
