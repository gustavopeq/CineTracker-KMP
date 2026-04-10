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

        val result = dao.getAllItems(1).first()

        assertEquals(3, result.size)
        assertEquals(2, result[0].contentId) // newest (300)
        assertEquals(3, result[1].contentId) // middle  (200)
        assertEquals(1, result[2].contentId) // oldest  (100)
    }

    @Test
    fun getAllItems_returnsOnlyItemsForGivenListId() = runBlocking {
        dao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 2, mediaType = "MOVIE", listId = 2, createdAt = 0))

        val result = dao.getAllItems(1).first()

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

        assertEquals(1, dao.getAllItems(1).first().size)
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    fun delete_removesOnlyItemMatchingCompositeKey() = runBlocking {
        dao.insert(ContentEntity(contentId = 1, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 1, mediaType = "SHOW", listId = 1, createdAt = 0))
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

        val result = dao.searchItems(contentId = 5, mediaType = "MOVIE").first()

        assertEquals(2, result.size)
        assertTrue(result.all { it.contentId == 5 })
    }

    @Test
    fun searchItems_doesNotReturnItemsWithDifferentMediaType() = runBlocking {
        dao.insert(ContentEntity(contentId = 5, mediaType = "MOVIE", listId = 1, createdAt = 0))
        dao.insert(ContentEntity(contentId = 5, mediaType = "SHOW", listId = 1, createdAt = 0))

        val result = dao.searchItems(contentId = 5, mediaType = "MOVIE").first()

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

    // ── cached fields ─────────────────────────────────────────────────────────

    @Test
    fun insert_storesCachedFieldsCorrectly() = runBlocking {
        val entity = ContentEntity(
            contentId = 500,
            mediaType = "MOVIE",
            listId = 1,
            title = "Test Movie",
            posterPath = "/poster.jpg",
            voteAverage = 8.5f
        )
        dao.insert(entity)

        val result = dao.getItem(500, "MOVIE", 1)
        assertNotNull(result)
        assertEquals("Test Movie", result!!.title)
        assertEquals("/poster.jpg", result.posterPath)
        assertEquals(8.5f, result.voteAverage, 0.001f)
    }

    @Test
    fun insert_usesDefaultsForCachedFieldsWhenNotProvided() = runBlocking {
        val entity = ContentEntity(
            contentId = 501,
            mediaType = "MOVIE",
            listId = 1
        )
        dao.insert(entity)

        val result = dao.getItem(501, "MOVIE", 1)
        assertNotNull(result)
        assertEquals("", result!!.title)
        assertNull(result.posterPath)
        assertEquals(0f, result.voteAverage, 0.001f)
    }

    @Test
    fun updateCachedFields_updatesAllMatchingEntities() = runBlocking {
        dao.insert(ContentEntity(contentId = 600, mediaType = "MOVIE", listId = 1))
        dao.insert(ContentEntity(contentId = 600, mediaType = "MOVIE", listId = 2))

        dao.updateCachedFields(
            contentId = 600,
            mediaType = "MOVIE",
            title = "Updated Title",
            posterPath = "/new_poster.jpg",
            voteAverage = 7.5f
        )

        val item1 = dao.getItem(600, "MOVIE", 1)
        val item2 = dao.getItem(600, "MOVIE", 2)
        assertEquals("Updated Title", item1!!.title)
        assertEquals("Updated Title", item2!!.title)
        assertEquals("/new_poster.jpg", item1.posterPath)
        assertEquals(7.5f, item1.voteAverage, 0.001f)
    }

    @Test
    fun updateCachedFields_doesNotAffectOtherContent() = runBlocking {
        dao.insert(
            ContentEntity(
                contentId = 700,
                mediaType = "MOVIE",
                listId = 1,
                title = "Original"
            )
        )
        dao.insert(
            ContentEntity(
                contentId = 701,
                mediaType = "SHOW",
                listId = 1,
                title = "Other"
            )
        )

        dao.updateCachedFields(
            contentId = 700,
            mediaType = "MOVIE",
            title = "Changed",
            posterPath = "/changed.jpg",
            voteAverage = 9.0f
        )

        val unchanged = dao.getItem(701, "SHOW", 1)
        assertEquals("Other", unchanged!!.title)
    }

    // ── getEntitiesWithMissingCachedFields ────────────────────────────────────

    // ── getAllSnapshot ─────────────────────────────────────────────────────────

    @Test
    fun getAllSnapshot_returnsAllItemsAcrossLists() = runBlocking {
        dao.insert(ContentEntity(contentId = 100, mediaType = "MOVIE", listId = 1))
        dao.insert(ContentEntity(contentId = 200, mediaType = "SHOW", listId = 2))

        val snapshot = dao.getAllSnapshot()

        assertEquals(2, snapshot.size)
        assertTrue(snapshot.any { it.contentId == 100 })
        assertTrue(snapshot.any { it.contentId == 200 })
    }

    // ── insertAll ─────────────────────────────────────────────────────────────

    @Test
    fun insertAll_insertsMultipleEntities() = runBlocking {
        val entities = listOf(
            ContentEntity(contentId = 100, mediaType = "MOVIE", listId = 1),
            ContentEntity(contentId = 200, mediaType = "SHOW", listId = 1)
        )

        dao.insertAll(entities)

        val snapshot = dao.getAllSnapshot()
        assertEquals(2, snapshot.size)
    }

    // ── getEntitiesWithMissingCachedFields ────────────────────────────────────

    @Test
    fun getEntitiesWithMissingCachedFields_returnsOnlyNullPosterPathEntities() = runBlocking {
        dao.insert(ContentEntity(contentId = 800, mediaType = "MOVIE", listId = 1, posterPath = null))
        dao.insert(ContentEntity(contentId = 801, mediaType = "SHOW", listId = 1, posterPath = "/poster.jpg"))
        dao.insert(ContentEntity(contentId = 802, mediaType = "MOVIE", listId = 2, posterPath = null))

        val stale = dao.getEntitiesWithMissingCachedFields()

        assertEquals(2, stale.size)
        assertTrue(stale.all { it.posterPath == null })
        assertTrue(stale.map { it.contentId }.containsAll(listOf(800, 802)))
    }

    @Test
    fun getEntitiesWithMissingCachedFields_returnsEmptyWhenAllHavePosterPath() = runBlocking {
        dao.insert(ContentEntity(contentId = 900, mediaType = "MOVIE", listId = 1, posterPath = "/a.jpg"))
        dao.insert(ContentEntity(contentId = 901, mediaType = "SHOW", listId = 1, posterPath = "/b.jpg"))

        val stale = dao.getEntitiesWithMissingCachedFields()

        assertTrue(stale.isEmpty())
    }
}
