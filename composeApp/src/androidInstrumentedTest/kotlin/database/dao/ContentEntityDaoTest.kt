package database.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import database.AppDatabase
import database.model.ContentEntity
import database.model.ListEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContentEntityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ContentEntityDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.contentEntityDao()
        runBlocking {
            database.listEntityDao().insertList(ListEntity(listId = 1, listName = "watchlist"))
            database.listEntityDao().insertList(ListEntity(listId = 2, listName = "watched"))
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ── getAllItems ────────────────────────────────────────────────────────────

    @Test
    fun getAllItems_returnsItemsOrderedByCreatedAtDesc() = runBlocking {
        dao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 100))
        dao.insert(ContentEntity(contentId = 2, mediaType = "MOVIE", listId = 1, createdAt = 300))
        dao.insert(ContentEntity(contentId = 3, mediaType = "MOVIE", listId = 1, createdAt = 200))

        val result = dao.getAllItems(1)

        assertEquals(3, result.size)
        assertEquals(2, result[0].contentId) // newest (300)
        assertEquals(3, result[1].contentId) // middle  (200)
        assertEquals(1, result[2].contentId) // oldest  (100)
    }

    @Test
    fun getAllItems_returnsOnlyItemsForGivenListId() = runBlocking {
        dao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 2, mediaType = "MOVIE", listId = 2, createdAt = 0))

        val result = dao.getAllItems(1)

        assertEquals(1, result.size)
        assertEquals(1, result[0].contentId)
    }

    // ── insert ────────────────────────────────────────────────────────────────

    @Test
    fun insert_silentlyIgnoresDuplicateWithSameExplicitPrimaryKey() = runBlocking {
        // OnConflictStrategy.IGNORE applies to PK conflicts, not FK violations.
        // This mirrors the reinsertItem undo flow, where an entity with an existing
        // contentEntityDbId is reinserted and should not crash or duplicate.
        val entity = ContentEntity(contentEntityDbId = 1, contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0)
        dao.insert(entity)
        dao.insert(entity) // same PK — silently ignored

        assertEquals(1, dao.getAllItems(1).size)
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    fun delete_removesOnlyItemMatchingCompositeKey() = runBlocking {
        dao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 1, mediaType = "SHOW",  listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 2, createdAt = 0))

        dao.delete(contentId = 1, mediaType = "MOVIE", listId = 1)

        val movieInList1 = dao.getItem(contentId = 1, mediaType = "MOVIE", listId = 1)
        assertNull(movieInList1)

        val showInList1 = dao.getItem(contentId = 1, mediaType = "SHOW", listId = 1)
        assertNotNull(showInList1)

        val movieInList2 = dao.getItem(contentId = 1, mediaType = "MOVIE", listId = 2)
        assertNotNull(movieInList2)
    }

    // ── searchItems ───────────────────────────────────────────────────────────

    @Test
    fun searchItems_returnsMatchingItemsAcrossAllLists() = runBlocking {
        dao.insert(ContentEntity(contentId = 5, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 5, mediaType = "MOVIE", listId = 2, createdAt = 0))
        dao.insert(ContentEntity(contentId = 6, mediaType = "MOVIE", listId = 1, createdAt = 0))

        val result = dao.searchItems(contentId = 5, mediaType = "MOVIE")

        assertEquals(2, result.size)
        assertTrue(result.all { it.contentId == 5 })
    }

    @Test
    fun searchItems_doesNotReturnItemsWithDifferentMediaType() = runBlocking {
        dao.insert(ContentEntity(contentId = 5, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 5, mediaType = "SHOW",  listId = 1, createdAt = 0))

        val result = dao.searchItems(contentId = 5, mediaType = "MOVIE")

        assertEquals(1, result.size)
        assertEquals("MOVIE", result[0].mediaType)
    }

    // ── getItem ───────────────────────────────────────────────────────────────

    @Test
    fun getItem_returnsNullWhenNotFound() = runBlocking {
        val result = dao.getItem(contentId = 99, mediaType = "MOVIE", listId = 1)
        assertNull(result)
    }

    @Test
    fun getItem_returnsCorrectItem() = runBlocking {
        dao.insert(ContentEntity(contentId = 3, mediaType = "SHOW", listId = 1, createdAt = 0))

        val result = dao.getItem(contentId = 3, mediaType = "SHOW", listId = 1)

        assertNotNull(result)
        assertEquals(3, result!!.contentId)
        assertEquals("SHOW", result.mediaType)
        assertEquals(1, result.listId)
    }
}
