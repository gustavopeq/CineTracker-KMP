package database.repository

import com.russhwolf.settings.Settings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class SettingsRepositoryImplTest {

    private val settings: Settings = mockk(relaxUnitFun = true)

    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = SettingsRepositoryImpl(settings)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `hasCompletedOnboarding returns true when flag is set`() {
        every { settings.getBoolean("onboarding_completed", false) } returns true

        val result = repository.hasCompletedOnboarding()

        assertTrue(result)
    }

    @Test
    fun `hasCompletedOnboarding returns false when flag is not set`() {
        every { settings.getBoolean("onboarding_completed", false) } returns false

        val result = repository.hasCompletedOnboarding()

        assertFalse(result)
    }

    @Test
    fun `setOnboardingCompleted stores true value`() {
        repository.setOnboardingCompleted()

        verify { settings.putBoolean("onboarding_completed", true) }
    }
}
