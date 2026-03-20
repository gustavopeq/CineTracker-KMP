package database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration5To6Test {

    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    // Creates a v5 DB with the two default lists and seeds content into them
    private fun createV5DefaultDb() {
        helper.createDatabase(TEST_DB, 5).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')") // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')") // listId = 2
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (1, 'MOVIE', 1, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (2, 'SHOW', 2, 0)")
            close()
        }
    }

    @Test
    fun migrate5To6_personalRatingsTableCreatedAndStartsEmpty() {
        createV5DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)

        val cursor = db.query("SELECT * FROM personal_ratings")
        assertEquals(0, cursor.count)
        cursor.close()

        db.close()
    }

    @Test
    fun migrate5To6_defaultListsAndTheirContentArePreserved() {
        createV5DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)

        val listsCursor = db.query("SELECT * FROM list_entity")
        assertEquals(2, listsCursor.count)
        listsCursor.close()

        val watchlistCursor = db.query("SELECT * FROM content_entity WHERE listId = 1")
        assertEquals(1, watchlistCursor.count)
        watchlistCursor.close()

        val watchedCursor = db.query("SELECT * FROM content_entity WHERE listId = 2")
        assertEquals(1, watchedCursor.count)
        watchedCursor.close()

        db.close()
    }

    @Test
    fun migrate5To6_customListsAndTheirContentArePreserved() {
        helper.createDatabase(TEST_DB, 5).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')") // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')") // listId = 2
            execSQL("INSERT INTO list_entity (listName) VALUES ('favorites')") // listId = 3
            execSQL("INSERT INTO list_entity (listName) VALUES ('horror night')") // listId = 4
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (10, 'MOVIE', 3, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (11, 'MOVIE', 3, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (20, 'SHOW', 4, 0)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)

        val listsCursor = db.query("SELECT * FROM list_entity")
        assertEquals(4, listsCursor.count)
        listsCursor.close()

        val favoritesCursor = db.query("SELECT * FROM content_entity WHERE listId = 3")
        assertEquals(2, favoritesCursor.count)
        favoritesCursor.close()

        val horrorCursor = db.query("SELECT * FROM content_entity WHERE listId = 4")
        assertEquals(1, horrorCursor.count)
        horrorCursor.close()

        db.close()
    }

    @Test
    fun migrate5To6_emptyCustomListSurvivesMigration() {
        helper.createDatabase(TEST_DB, 5).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')") // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')") // listId = 2
            execSQL("INSERT INTO list_entity (listName) VALUES ('empty list')") // listId = 3, no content
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)

        val listsCursor = db.query("SELECT * FROM list_entity")
        assertEquals(3, listsCursor.count)
        listsCursor.close()

        val contentCursor = db.query("SELECT * FROM content_entity WHERE listId = 3")
        assertEquals(0, contentCursor.count)
        contentCursor.close()

        db.close()
    }

    @Test
    fun migrate5To6_contentSpreadAcrossAllListsIsFullyPreserved() {
        helper.createDatabase(TEST_DB, 5).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')") // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')") // listId = 2
            execSQL("INSERT INTO list_entity (listName) VALUES ('favorites')") // listId = 3
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (1, 'MOVIE', 1, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (2, 'SHOW', 1, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (3, 'MOVIE', 2, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (4, 'MOVIE', 3, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (5, 'SHOW', 3, 0)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)

        val totalContent = db.query("SELECT * FROM content_entity")
        assertEquals(5, totalContent.count)
        totalContent.close()

        db.close()
    }
}
