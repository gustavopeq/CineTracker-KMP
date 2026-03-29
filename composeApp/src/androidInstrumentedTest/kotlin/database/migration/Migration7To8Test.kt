package database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration7To8Test {

    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    private fun createV7Db() {
        helper.createDatabase(TEST_DB, 7).apply {
            execSQL("INSERT INTO list_entity (listName, isDefault) VALUES ('watchlist', 1)")
            execSQL("INSERT INTO list_entity (listName, isDefault) VALUES ('watched', 1)")
            execSQL(
                "INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (100, 'MOVIE', 1, 1000)"
            )
            execSQL(
                "INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (200, 'SHOW', 2, 2000)"
            )
            close()
        }
    }

    @Test
    fun migrate7To8_columnsAddedWithCorrectDefaults() {
        createV7Db()

        val db = helper.runMigrationsAndValidate(TEST_DB, 8, true, MIGRATION_7_8)

        val cursor = db.query("SELECT title, posterPath, voteAverage FROM content_entity WHERE contentId = 100")
        cursor.moveToFirst()
        assertEquals("", cursor.getString(0))
        assertNull(cursor.getString(1))
        assertEquals(0f, cursor.getFloat(2), 0.001f)
        cursor.close()
        db.close()
    }

    @Test
    fun migrate7To8_existingDataPreserved() {
        createV7Db()

        val db = helper.runMigrationsAndValidate(TEST_DB, 8, true, MIGRATION_7_8)

        val cursor = db.query("SELECT contentId, mediaType, listId, createdAt FROM content_entity ORDER BY contentId")
        assertEquals(2, cursor.count)

        cursor.moveToFirst()
        assertEquals(100, cursor.getInt(0))
        assertEquals("MOVIE", cursor.getString(1))
        assertEquals(1, cursor.getInt(2))
        assertEquals(1000L, cursor.getLong(3))

        cursor.moveToNext()
        assertEquals(200, cursor.getInt(0))
        assertEquals("SHOW", cursor.getString(1))
        assertEquals(2, cursor.getInt(2))
        assertEquals(2000L, cursor.getLong(3))

        cursor.close()
        db.close()
    }

    @Test
    fun migrate7To8_listsArePreserved() {
        createV7Db()

        val db = helper.runMigrationsAndValidate(TEST_DB, 8, true, MIGRATION_7_8)

        val cursor = db.query("SELECT * FROM list_entity")
        assertEquals(2, cursor.count)
        cursor.close()
        db.close()
    }
}
