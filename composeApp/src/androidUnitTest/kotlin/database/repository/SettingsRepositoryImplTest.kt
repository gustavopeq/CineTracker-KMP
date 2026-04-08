package database.repository

import com.russhwolf.settings.Settings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

    @Test
    fun `hasSeenDetailsOverlay returns true when flag is set`() {
        every { settings.getBoolean("details_overlay_seen", false) } returns true

        val result = repository.hasSeenDetailsOverlay()

        assertTrue(result)
    }

    @Test
    fun `hasSeenDetailsOverlay returns false when flag is not set`() {
        every { settings.getBoolean("details_overlay_seen", false) } returns false

        val result = repository.hasSeenDetailsOverlay()

        assertFalse(result)
    }

    @Test
    fun `setDetailsOverlaySeen stores true value`() {
        repository.setDetailsOverlaySeen()

        verify { settings.putBoolean("details_overlay_seen", true) }
    }

    @Test
    fun `areEngagementRemindersEnabled returns false by default`() {
        every { settings.getBoolean("engagement_reminders_enabled", false) } returns false

        val result = repository.areEngagementRemindersEnabled()

        assertFalse(result)
    }

    @Test
    fun `areEngagementRemindersEnabled returns true when enabled`() {
        every { settings.getBoolean("engagement_reminders_enabled", false) } returns true

        val result = repository.areEngagementRemindersEnabled()

        assertTrue(result)
    }

    @Test
    fun `setEngagementRemindersEnabled stores true`() {
        repository.setEngagementRemindersEnabled(true)

        verify { settings.putBoolean("engagement_reminders_enabled", true) }
    }

    @Test
    fun `setEngagementRemindersEnabled stores false`() {
        repository.setEngagementRemindersEnabled(false)

        verify { settings.putBoolean("engagement_reminders_enabled", false) }
    }

    @Test
    fun `getAppLanguage returns stored value when present`() {
        every { settings.getStringOrNull("app_language") } returns "pt-BR"

        val result = repository.getAppLanguage()

        assertEquals("pt-BR", result)
    }

    @Test
    fun `getAppLanguage returns null when no value stored`() {
        every { settings.getStringOrNull("app_language") } returns null

        val result = repository.getAppLanguage()

        assertNull(result)
    }

    @Test
    fun `setAppLanguage stores value`() {
        repository.setAppLanguage("es-MX")

        verify { settings.putString("app_language", "es-MX") }
    }

    @Test
    fun `getAppRegion returns stored value when present`() {
        every { settings.getStringOrNull("app_region") } returns "BR"

        val result = repository.getAppRegion()

        assertEquals("BR", result)
    }

    @Test
    fun `getAppRegion returns null when no value stored`() {
        every { settings.getStringOrNull("app_region") } returns null

        val result = repository.getAppRegion()

        assertNull(result)
    }

    @Test
    fun `setAppRegion stores value`() {
        repository.setAppRegion("US")

        verify { settings.putString("app_region", "US") }
    }
}
