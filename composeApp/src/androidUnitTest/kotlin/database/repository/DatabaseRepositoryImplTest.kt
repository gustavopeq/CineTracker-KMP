package database.repository

import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import database.dao.ContentEntityDao
import database.dao.ListEntityDao
import database.model.ListEntity
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
    fun `moveItemToList executes delete-insert-delete in order`() = runTest {
        val oldEntity = fakeContentEntity(contentId = 1, listId = 1)
        // First deleteItem (from newListId=2): item may not exist there
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 2) } returns null
        // Second deleteItem (from currentListId=1): item exists
        coEvery { contentEntityDao.getItem(1, MediaType.MOVIE.name, 1) } returns oldEntity

        repository.moveItemToList(1, MediaType.MOVIE, currentListId = 1, newListId = 2)

        coVerify(ordering = Ordering.SEQUENCE) {
            // 1. Delete from new list (cleanup any duplicate)
            contentEntityDao.getItem(1, MediaType.MOVIE.name, 2)
            // 2. Insert into new list
            contentEntityDao.insert(any())
            // 3. Delete from old list
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
}
