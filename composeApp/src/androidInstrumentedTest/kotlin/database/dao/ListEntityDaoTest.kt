package database.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import database.AppDatabase
import database.model.ContentEntity
import database.model.ListEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListEntityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ListEntityDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.listEntityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ── getAllLists ───────────────────────────────────────────────────────────

    @Test
    fun getAllLists_returnsAllInsertedLists() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "watchlist"))
        dao.insertList(ListEntity(listId = 2, listName = "watched"))
        dao.insertList(ListEntity(listId = 3, listName = "favorites"))

        val result = dao.getAllLists().first()

        assertEquals(3, result.size)
    }

    @Test
    fun getAllLists_returnsEmptyWhenNoListsExist() = runBlocking {
        val result = dao.getAllLists().first()

        assertEquals(0, result.size)
    }

    // ── getListCountByName ────────────────────────────────────────────────────

    @Test
    fun getListCountByName_isCaseInsensitive() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "watchlist"))

        assertEquals(1, dao.getListCountByName("WATCHLIST"))
        assertEquals(1, dao.getListCountByName("Watchlist"))
        assertEquals(1, dao.getListCountByName("watchlist"))
    }

    @Test
    fun getListCountByName_returnsZeroWhenNotFound() = runBlocking {
        val count = dao.getListCountByName("nonexistent")

        assertEquals(0, count)
    }

    // ── insertList ────────────────────────────────────────────────────────────

    @Test
    fun insertList_throwsOnPrimaryKeyConflict() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "first"))

        var threw = false
        try {
            dao.insertList(ListEntity(listId = 1, listName = "second"))
        } catch (e: Exception) {
            threw = true
        }

        assertTrue(threw)
        // Original list is preserved
        assertEquals(1, dao.getListCountByName("first"))
        assertEquals(0, dao.getListCountByName("second"))
    }

    // ── deleteList ────────────────────────────────────────────────────────────

    @Test
    fun deleteList_removesTheList() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "watchlist"))

        dao.deleteList(1)

        assertEquals(0, dao.getAllLists().first().size)
    }

    @Test
    fun deleteList_cascadesAndRemovesAllAssociatedContent() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "watchlist"))
        val contentDao = database.contentEntityDao()
        contentDao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0))
        contentDao.insert(ContentEntity(contentId = 2, mediaType = "SHOW", listId = 1, createdAt = 0))

        dao.deleteList(1)

        assertEquals(0, contentDao.getAllItems(1).first().size)
    }

    // ── isDefault ─────────────────────────────────────────────────────────────

    @Test
    fun insertList_storesIsDefaultTrue() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "watchlist", isDefault = true))

        val result = dao.getAllLists().first()

        assertTrue(result[0].isDefault)
    }

    @Test
    fun insertList_isDefaultFalseByDefault() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "favorites"))

        val result = dao.getAllLists().first()

        assertFalse(result[0].isDefault)
    }

    @Test
    fun deleteList_doesNotAffectContentInOtherLists() = runBlocking {
        dao.insertList(ListEntity(listId = 1, listName = "watchlist"))
        dao.insertList(ListEntity(listId = 2, listName = "watched"))
        val contentDao = database.contentEntityDao()
        contentDao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0))
        contentDao.insert(ContentEntity(contentId = 2, mediaType = "MOVIE", listId = 2, createdAt = 0))

        dao.deleteList(1)

        assertEquals(1, contentDao.getAllItems(2).first().size)
    }
}
