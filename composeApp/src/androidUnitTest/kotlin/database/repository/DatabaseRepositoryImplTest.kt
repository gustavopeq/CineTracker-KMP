package database.repository

import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import common.util.fakeListEntity
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
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

class DatabaseRepositoryImplTest {

    private val contentEntityDao: ContentEntityDao = mockk(relaxUnitFun = true)
    private val listEntityDao: ListEntityDao = mockk(relaxUnitFun = true)

    private lateinit var repository: DatabaseRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = DatabaseRepositoryImpl(contentEntityDao, listEntityDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ── addNewList ────────────────────────────────────────────────────────────

    @Test
    fun `addNewList converts name to lowercase`() = runTest {
        coEvery { listEntityDao.getListCountByName(any()) } returns 0

        repository.addNewList("My Favorites")

        coVerify { listEntityDao.getListCountByName("my favorites") }
    }

    @Test
    fun `addNewList returns false for duplicate name`() = runTest {
        coEvery { listEntityDao.getListCountByName("duplicated") } returns 1

        val result = repository.addNewList("Duplicated")

        assertFalse(result)
        coVerify(exactly = 0) { listEntityDao.insertList(any()) }
    }

    @Test
    fun `addNewList returns true and inserts for unique name`() = runTest {
        coEvery { listEntityDao.getListCountByName("new list") } returns 0

        val result = repository.addNewList("New List")

        assertTrue(result)
        coVerify { listEntityDao.insertList(match { it.listName == "new list" }) }
    }

    // ── deleteItem ────────────────────────────────────────────────────────────

    @Test
    fun `deleteItem returns entity when found and deletes it`() = runTest {
        val entity = fakeContentEntity(contentId = 1, listId = 1)
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 1) } returns entity

        val result = repository.deleteItem(1, MediaType.MOVIE, 1)

        assertNotNull(result)
        assertEquals(1, result.contentId)
        coVerify { contentEntityDao.delete(1, MediaType.MOVIE.name, 1) }
    }

    @Test
    fun `deleteItem returns null when item not found`() = runTest {
        coEvery { contentEntityDao.getItem(any(), any(), any()) } returns null

        val result = repository.deleteItem(99, MediaType.MOVIE, 1)

        assertNull(result)
        coVerify(exactly = 0) { contentEntityDao.delete(any(), any(), any()) }
    }

    // ── moveItemToList ────────────────────────────────────────────────────────

    @Test
    fun `moveItemToList preserves cached fields and executes in order`() = runTest {
        val oldEntity = fakeContentEntity(contentId = 1, listId = 1)
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 1) } returns oldEntity
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 2) } returns null

        repository.moveItemToList(1, MediaType.MOVIE, currentListId = 1, newListId = 2)

        coVerify(ordering = Ordering.SEQUENCE) {
            // 1. Fetch existing item to preserve cached fields
            contentEntityDao.getItem(1, MediaType.MOVIE.name, 1)
            // 2. Delete from new list (cleanup any duplicate)
            contentEntityDao.getItem(1, MediaType.MOVIE.name, 2)
            // 3. Insert into new list with cached fields
            contentEntityDao.insert(any())
            // 4. Delete from old list
            contentEntityDao.getItem(1, MediaType.MOVIE.name, 1)
            contentEntityDao.delete(1, MediaType.MOVIE.name, 1)
        }
    }

    @Test
    fun `moveItemToList returns the deleted item from old list`() = runTest {
        val oldEntity = fakeContentEntity(contentId = 5, listId = 1)
        coEvery { contentEntityDao.getItem(5, MediaType.SHOW.name, 2) } returns null
        coEvery { contentEntityDao.getItem(5, MediaType.SHOW.name, 1) } returns oldEntity

        val result = repository.moveItemToList(5, MediaType.SHOW, currentListId = 1, newListId = 2)

        assertNotNull(result)
        assertEquals(5, result.contentId)
    }

    // ── insertItem ────────────────────────────────────────────────────────────

    @Test
    fun `insertItem inserts entity with correct contentId, mediaType and listId`() = runTest {
        repository.insertItem(contentId = 10, mediaType = MediaType.MOVIE, listId = 1)

        coVerify {
            contentEntityDao.insert(
                match { it.contentId == 10 && it.mediaType == "MOVIE" && it.listId == 1 }
            )
        }
    }

    // ── reinsertItem ──────────────────────────────────────────────────────────

    @Test
    fun `reinsertItem inserts the given entity as-is`() = runTest {
        val entity = fakeContentEntity(contentId = 7, listId = 2)

        repository.reinsertItem(entity)

        coVerify { contentEntityDao.insert(entity) }
    }

    // ── getAllItemsByListId ────────────────────────────────────────────────────

    @Test
    fun `getAllItemsByListId returns items from DAO for given listId`() = runTest {
        val items = listOf(fakeContentEntity(contentId = 1, listId = 3), fakeContentEntity(contentId = 2, listId = 3))
        every { contentEntityDao.getAllItems(3) } returns flowOf(items)

        val result = repository.getAllItemsByListId(3).first()

        assertEquals(items, result)
    }

    // ── searchItems ───────────────────────────────────────────────────────────

    @Test
    fun `searchItems passes contentId and mediaType name to DAO`() = runTest {
        val items = listOf(fakeContentEntity(contentId = 5, listId = 1), fakeContentEntity(contentId = 5, listId = 2))
        every { contentEntityDao.searchItems(5, MediaType.SHOW.name) } returns flowOf(items)

        val result = repository.searchItems(contentId = 5, mediaType = MediaType.SHOW).first()

        assertEquals(items, result)
        verify { contentEntityDao.searchItems(5, "SHOW") }
    }

    // ── getAllLists ───────────────────────────────────────────────────────────

    @Test
    fun `getAllLists returns all lists from DAO`() = runTest {
        val lists = listOf(fakeListEntity(listId = 1, name = "watchlist"), fakeListEntity(listId = 2, name = "watched"))
        every { listEntityDao.getAllLists() } returns flowOf(lists)

        val result = repository.getAllLists().first()

        assertEquals(lists, result)
    }

    // ── deleteList ────────────────────────────────────────────────────────────

    @Test
    fun `deleteList delegates to listEntityDao with given listId`() = runTest {
        repository.deleteList(listId = 3)

        coVerify { listEntityDao.deleteList(3) }
    }

    // ── updateCachedFields ────────────────────────────────────────────────────

    @Test
    fun `updateCachedFields delegates to contentEntityDao`() = runTest {
        coEvery { contentEntityDao.updateCachedFields(any(), any(), any(), any(), any()) } just runs

        repository.updateCachedFields(
            contentId = 1,
            mediaType = MediaType.MOVIE,
            title = "Updated",
            posterPath = "/poster.jpg",
            voteAverage = 8.0f
        )

        coVerify {
            contentEntityDao.updateCachedFields(1, "MOVIE", "Updated", "/poster.jpg", 8.0f)
        }
    }

    // ── insertItem with cached fields ─────────────────────────────────────────

    @Test
    fun `insertItem passes cached fields to entity`() = runTest {
        repository.insertItem(
            contentId = 1,
            mediaType = MediaType.MOVIE,
            listId = 1,
            title = "Test",
            posterPath = "/poster.jpg",
            voteAverage = 7.5f
        )

        coVerify {
            contentEntityDao.insert(
                match {
                    it.contentId == 1 &&
                        it.title == "Test" &&
                        it.posterPath == "/poster.jpg" &&
                        it.voteAverage == 7.5f
                }
            )
        }
    }

    @Test
    fun `insertItem uses empty defaults when cached fields not provided`() = runTest {
        repository.insertItem(contentId = 5, mediaType = MediaType.SHOW, listId = 2)

        coVerify {
            contentEntityDao.insert(
                match {
                    it.contentId == 5 &&
                        it.mediaType == "SHOW" &&
                        it.listId == 2 &&
                        it.title == "" &&
                        it.posterPath == null &&
                        it.voteAverage == 0f
                }
            )
        }
    }

    // ── moveItemToList — cached field preservation ────────────────────────────

    @Test
    fun `moveItemToList preserves title posterPath and voteAverage from source entity`() = runTest {
        val sourceEntity = fakeContentEntity(
            contentId = 1,
            listId = 1,
            title = "Cached Title",
            posterPath = "/cached_poster.jpg",
            voteAverage = 9.2f
        )
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 1) } returns sourceEntity
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 2) } returns null

        repository.moveItemToList(1, MediaType.MOVIE, currentListId = 1, newListId = 2)

        coVerify {
            contentEntityDao.insert(
                match {
                    it.contentId == 1 &&
                        it.listId == 2 &&
                        it.title == "Cached Title" &&
                        it.posterPath == "/cached_poster.jpg" &&
                        it.voteAverage == 9.2f
                }
            )
        }
    }

    @Test
    fun `moveItemToList handles null existing item gracefully`() = runTest {
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 1) } returns null
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 2) } returns null

        repository.moveItemToList(1, MediaType.MOVIE, currentListId = 1, newListId = 2)

        coVerify {
            contentEntityDao.insert(
                match {
                    it.contentId == 1 &&
                        it.listId == 2 &&
                        it.title == "" &&
                        it.posterPath == null &&
                        it.voteAverage == 0f
                }
            )
        }
    }
}
