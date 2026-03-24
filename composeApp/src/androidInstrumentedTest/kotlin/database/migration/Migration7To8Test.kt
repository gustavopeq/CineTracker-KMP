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
class Migration7To8Test {

    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    private fun createV7DefaultDb() {
        helper.createDatabase(TEST_DB, 7).apply {
            execSQL("INSERT INTO list_entity (listName, isDefault) VALUES ('watchlist', 1)")
            execSQL("INSERT INTO list_entity (listName, isDefault) VALUES ('watched', 1)")
            execSQL("INSERT INTO content_entity (contentId, mediaType, listId, createdAt) VALUES (1, 'MOVIE', 1, 0)")
            close()
        }
    }

    @Test
    fun migrate7To8_settingsTableIsCreated() {
        createV7DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 8, true, MIGRATION_7_8)

        db.execSQL("INSERT INTO settings_entity (`key`, value) VALUES ('test_key', 'test_value')")

        val cursor = db.query("SELECT * FROM settings_entity WHERE `key` = 'test_key'")
        assertEquals(1, cursor.count)
        cursor.moveToFirst()
        assertEquals("test_key", cursor.getString(cursor.getColumnIndexOrThrow("key")))
        assertEquals("test_value", cursor.getString(cursor.getColumnIndexOrThrow("value")))

        cursor.close()
        db.close()
    }

    @Test
    fun migrate7To8_existingDataIsPreserved() {
        createV7DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 8, true, MIGRATION_7_8)

        val listsCursor = db.query("SELECT * FROM list_entity")
        assertEquals(2, listsCursor.count)
        listsCursor.close()

        val contentCursor = db.query("SELECT * FROM content_entity")
        assertEquals(1, contentCursor.count)
        contentCursor.close()

        db.close()
    }

    @Test
    fun migrate7To8_settingsTableStartsEmpty() {
        createV7DefaultDb()

        val db = helper.runMigrationsAndValidate(TEST_DB, 8, true, MIGRATION_7_8)

        val cursor = db.query("SELECT * FROM settings_entity")
        assertEquals(0, cursor.count)

        cursor.close()
        db.close()
    }
}
