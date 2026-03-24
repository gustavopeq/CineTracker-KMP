package database.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import database.AppDatabase
import database.model.SettingsEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsEntityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: SettingsDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.settingsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ── getSetting ──────────────────────────────────────────────────────────

    @Test
    fun getSetting_returnsEntityWhenFound() = runBlocking {
        dao.insertSetting(SettingsEntity(key = "test_key", value = "test_value"))

        val result = dao.getSetting("test_key")

        assertNotNull(result)
        assertEquals("test_value", result!!.value)
    }

    @Test
    fun getSetting_returnsNullWhenNotFound() = runBlocking {
        val result = dao.getSetting("nonexistent_key")

        assertNull(result)
    }

    // ── insertSetting ───────────────────────────────────────────────────────

    @Test
    fun insertSetting_replacesExistingValueForSameKey() = runBlocking {
        dao.insertSetting(SettingsEntity(key = "test_key", value = "old_value"))
        dao.insertSetting(SettingsEntity(key = "test_key", value = "new_value"))

        val result = dao.getSetting("test_key")

        assertNotNull(result)
        assertEquals("new_value", result!!.value)
    }

    @Test
    fun insertSetting_doesNotAffectOtherKeys() = runBlocking {
        dao.insertSetting(SettingsEntity(key = "key_a", value = "value_a"))
        dao.insertSetting(SettingsEntity(key = "key_b", value = "value_b"))

        val resultA = dao.getSetting("key_a")
        val resultB = dao.getSetting("key_b")

        assertNotNull(resultA)
        assertNotNull(resultB)
        assertEquals("value_a", resultA!!.value)
        assertEquals("value_b", resultB!!.value)
    }
}
