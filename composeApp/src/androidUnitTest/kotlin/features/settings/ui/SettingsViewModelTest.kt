package features.settings.ui

import common.util.platform.AppNotifications
import features.settings.domain.LanguageItem
import features.settings.domain.RegionItem
import features.settings.domain.SettingsInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private val settingsInteractor: SettingsInteractor = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(AppNotifications)
        every { AppNotifications.scheduleEngagementReminders() } returns Unit
        every { AppNotifications.cancelEngagementReminders() } returns Unit
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun setupDefaultMocks(
        language: String = "en-US",
        region: String = "US",
        notificationsEnabled: Boolean = false,
    ) {
        every { settingsInteractor.getAppLanguage() } returns language
        every { settingsInteractor.getAppRegion() } returns region
        every { settingsInteractor.areNotificationsEnabled() } returns notificationsEnabled
        every { settingsInteractor.getSupportedLanguages() } returns listOf(
            LanguageItem("en-US", "English (US)"),
            LanguageItem("pt-BR", "Portugu\u00eas (Brasil)"),
            LanguageItem("es-ES", "Espa\u00f1ol (Espa\u00f1a)"),
        )
        every { settingsInteractor.getSupportedRegions() } returns listOf(
            RegionItem("US", "United States"),
            RegionItem("BR", "Brazil"),
            RegionItem("ES", "Spain"),
        )
    }

    private fun createViewModel(): SettingsViewModel {
        return SettingsViewModel(settingsInteractor)
    }

    @Test
    fun `init loads current language display name`() {
        setupDefaultMocks(language = "pt-BR")

        val viewModel = createViewModel()

        assertEquals("Portugu\u00eas (Brasil)", viewModel.currentLanguageDisplay.value)
    }

    @Test
    fun `init loads current region display name`() {
        setupDefaultMocks(region = "BR")

        val viewModel = createViewModel()

        assertEquals("Brazil", viewModel.currentRegionDisplay.value)
    }

    @Test
    fun `init loads notification state`() {
        setupDefaultMocks(notificationsEnabled = true)

        val viewModel = createViewModel()

        assertTrue(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `enabling notifications calls interactor and schedules reminders`() {
        setupDefaultMocks()
        val viewModel = createViewModel()

        viewModel.onNotificationPermissionResult(true)

        verify { settingsInteractor.setNotificationsEnabled(true) }
        verify { AppNotifications.scheduleEngagementReminders() }
        assertTrue(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `disabling notifications calls interactor and cancels reminders`() {
        setupDefaultMocks(notificationsEnabled = true)
        val viewModel = createViewModel()

        viewModel.disableNotifications()

        verify { settingsInteractor.setNotificationsEnabled(false) }
        verify { AppNotifications.cancelEngagementReminders() }
        assertFalse(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `denied notification permission does not enable notifications`() {
        setupDefaultMocks()
        val viewModel = createViewModel()

        viewModel.onNotificationPermissionResult(false)

        verify(exactly = 0) { settingsInteractor.setNotificationsEnabled(any()) }
        assertFalse(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `refreshSettings updates display values after language change`() {
        setupDefaultMocks(language = "en-US", region = "US")
        val viewModel = createViewModel()

        assertEquals("English (US)", viewModel.currentLanguageDisplay.value)
        assertEquals("United States", viewModel.currentRegionDisplay.value)

        every { settingsInteractor.getAppLanguage() } returns "pt-BR"
        every { settingsInteractor.getAppRegion() } returns "BR"

        viewModel.refreshSettings()

        assertEquals("Portugu\u00eas (Brasil)", viewModel.currentLanguageDisplay.value)
        assertEquals("Brazil", viewModel.currentRegionDisplay.value)
    }
}
