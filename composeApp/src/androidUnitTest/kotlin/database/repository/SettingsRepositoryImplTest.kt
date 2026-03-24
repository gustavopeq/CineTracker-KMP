package database.repository

import database.dao.SettingsDao
import database.model.SettingsEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SettingsRepositoryImplTest {

    private val settingsDao: SettingsDao = mockk(relaxUnitFun = true)

    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = SettingsRepositoryImpl(settingsDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `hasCompletedOnboarding returns true when flag is set`() = runTest {
        coEvery { settingsDao.getSetting("onboarding_completed") } returns SettingsEntity(
            key = "onboarding_completed",
            value = "true"
        )

        val result = repository.hasCompletedOnboarding()

        assertTrue(result)
    }

    @Test
    fun `hasCompletedOnboarding returns false when flag is not set`() = runTest {
        coEvery { settingsDao.getSetting("onboarding_completed") } returns null

        val result = repository.hasCompletedOnboarding()

        assertFalse(result)
    }

    @Test
    fun `hasCompletedOnboarding returns false when value is not true`() = runTest {
        coEvery { settingsDao.getSetting("onboarding_completed") } returns SettingsEntity(
            key = "onboarding_completed",
            value = "false"
        )

        val result = repository.hasCompletedOnboarding()

        assertFalse(result)
    }

    @Test
    fun `setOnboardingCompleted inserts correct entity`() = runTest {
        repository.setOnboardingCompleted()

        coVerify {
            settingsDao.insertSetting(
                match { it.key == "onboarding_completed" && it.value == "true" }
            )
        }
    }
}
