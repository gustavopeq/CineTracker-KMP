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
class Migration6To7Test {

    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    private fun createV6DefaultDb() {
        helper.createDatabase(TEST_DB, 6).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')")  // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')")     // listId = 2
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (1, 'MOVIE', 1, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (2, 'SHOW', 2, 0)")
            close()
        }
    }

    @Test
    fun migrate6To7_defaultListsMarkedAsDefault() {
        createV6DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 7, true, MIGRATION_6_7)

        val cursor = db.query("SELECT listName, isDefault FROM list_entity ORDER BY listId ASC")
        assertEquals(2, cursor.count)

        cursor.moveToFirst()
        assertEquals("watchlist", cursor.getString(0))
        assertEquals(1, cursor.getInt(1))

        cursor.moveToNext()
        assertEquals("watched", cursor.getString(0))
        assertEquals(1, cursor.getInt(1))

        cursor.close()
        db.close()
    }

    @Test
    fun migrate6To7_customListsHaveIsDefaultFalse() {
        helper.createDatabase(TEST_DB, 6).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')")   // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')")      // listId = 2
            execSQL("INSERT INTO list_entity (listName) VALUES ('favorites')")    // listId = 3
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 7, true, MIGRATION_6_7)

        val cursor = db.query("SELECT isDefault FROM list_entity WHERE listName = 'favorites'")
        cursor.moveToFirst()
        assertEquals(0, cursor.getInt(0))
        cursor.close()
        db.close()
    }

    @Test
    fun migrate6To7_contentAndListsArePreserved() {
        createV6DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 7, true, MIGRATION_6_7)

        val listsCursor = db.query("SELECT * FROM list_entity")
        assertEquals(2, listsCursor.count)
        listsCursor.close()

        val contentCursor = db.query("SELECT * FROM content_entity")
        assertEquals(2, contentCursor.count)
        contentCursor.close()

        db.close()
    }

    @Test
    fun migrate6To7_customListsAndTheirContentArePreserved() {
        helper.createDatabase(TEST_DB, 6).apply {
            execSQL("INSERT INTO list_entity (listName) VALUES ('watchlist')")   // listId = 1
            execSQL("INSERT INTO list_entity (listName) VALUES ('watched')")      // listId = 2
            execSQL("INSERT INTO list_entity (listName) VALUES ('favorites')")    // listId = 3
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (10, 'MOVIE', 3, 0)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (11, 'SHOW', 3, 0)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 7, true, MIGRATION_6_7)

        val cursor = db.query("SELECT * FROM content_entity WHERE listId = 3")
        assertEquals(2, cursor.count)
        cursor.close()
        db.close()
    }
}
