package features.settings.domain

import common.util.platform.PlatformUtils
import database.repository.SettingsRepository
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

class SettingsInteractorTest {

    private val settingsRepository: SettingsRepository = mockk(relaxUnitFun = true)

    private lateinit var interactor: SettingsInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(PlatformUtils)
        interactor = SettingsInteractor(settingsRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // region getAppLanguage

    @Test
    fun `getAppLanguage returns stored value when present`() {
        every { settingsRepository.getAppLanguage() } returns "pt-BR"

        val result = interactor.getAppLanguage()

        assertEquals("pt-BR", result)
    }

    @Test
    fun `getAppLanguage returns device locale when stored is null and locale is supported`() {
        every { settingsRepository.getAppLanguage() } returns null
        every { PlatformUtils.getLocale() } returns "es-MX"

        val result = interactor.getAppLanguage()

        assertEquals("es-MX", result)
    }

    @Test
    fun `getAppLanguage falls back to language family when locale not supported`() {
        every { settingsRepository.getAppLanguage() } returns null
        every { PlatformUtils.getLocale() } returns "pt-PT"
        every { PlatformUtils.getUserLanguage() } returns "pt"

        val result = interactor.getAppLanguage()

        assertEquals("pt-BR", result)
    }

    @Test
    fun `getAppLanguage falls back to es-ES for unsupported Spanish locale`() {
        every { settingsRepository.getAppLanguage() } returns null
        every { PlatformUtils.getLocale() } returns "es-AR"
        every { PlatformUtils.getUserLanguage() } returns "es"

        val result = interactor.getAppLanguage()

        assertEquals("es-ES", result)
    }

    @Test
    fun `getAppLanguage falls back to en-US for unsupported language`() {
        every { settingsRepository.getAppLanguage() } returns null
        every { PlatformUtils.getLocale() } returns "fr-FR"
        every { PlatformUtils.getUserLanguage() } returns "fr"

        val result = interactor.getAppLanguage()

        assertEquals("en-US", result)
    }

    // endregion

    // region setAppLanguage

    @Test
    fun `setAppLanguage delegates to repository`() {
        interactor.setAppLanguage("es-MX")

        verify { settingsRepository.setAppLanguage("es-MX") }
    }

    // endregion

    // region getAppRegion

    @Test
    fun `getAppRegion returns stored region when present`() {
        every { settingsRepository.getAppRegion() } returns "BR"

        val result = interactor.getAppRegion()

        assertEquals("BR", result)
    }

    @Test
    fun `getAppRegion returns device country when stored is null and country is in list`() {
        every { settingsRepository.getAppRegion() } returns null
        every { PlatformUtils.getUserCountry() } returns "GB"

        val result = interactor.getAppRegion()

        assertEquals("GB", result)
    }

    @Test
    fun `getAppRegion falls back to US when device country not in list`() {
        every { settingsRepository.getAppRegion() } returns null
        every { PlatformUtils.getUserCountry() } returns "ZZ"

        val result = interactor.getAppRegion()

        assertEquals("US", result)
    }

    // endregion

    // region setAppRegion

    @Test
    fun `setAppRegion delegates to repository`() {
        interactor.setAppRegion("BR")

        verify { settingsRepository.setAppRegion("BR") }
    }

    // endregion

    // region getSupportedLanguages

    @Test
    fun `getSupportedLanguages returns 15 entries`() {
        val languages = interactor.getSupportedLanguages()

        assertEquals(15, languages.size)
    }

    @Test
    fun `getSupportedLanguages contains en-US with correct display name`() {
        val languages = interactor.getSupportedLanguages()
        val enUS = languages.find { it.tag == "en-US" }

        assertEquals("English (US)", enUS?.displayName)
    }

    @Test
    fun `getSupportedLanguages contains pt-BR with correct display name`() {
        val languages = interactor.getSupportedLanguages()
        val ptBR = languages.find { it.tag == "pt-BR" }

        assertEquals("Portugu\u00eas (Brasil)", ptBR?.displayName)
    }

    // endregion

    // region getSupportedRegions

    @Test
    fun `getSupportedRegions returns 40 entries`() {
        every { PlatformUtils.getDisplayCountry(any()) } answers { firstArg() }

        val regions = interactor.getSupportedRegions()

        assertEquals(40, regions.size)
    }

    @Test
    fun `getSupportedRegions entries are sorted alphabetically by displayName`() {
        every { PlatformUtils.getDisplayCountry(any()) } answers { firstArg() }

        val regions = interactor.getSupportedRegions()
        val displayNames = regions.map { it.displayName }

        assertEquals(displayNames.sorted(), displayNames)
    }

    // endregion

    // region areNotificationsEnabled

    @Test
    fun `areNotificationsEnabled returns true when repository returns true`() {
        every { settingsRepository.areEngagementRemindersEnabled() } returns true

        val result = interactor.areNotificationsEnabled()

        assertTrue(result)
    }

    @Test
    fun `areNotificationsEnabled returns false when repository returns false`() {
        every { settingsRepository.areEngagementRemindersEnabled() } returns false

        val result = interactor.areNotificationsEnabled()

        assertFalse(result)
    }

    // endregion

    // region setNotificationsEnabled

    @Test
    fun `setNotificationsEnabled delegates true to repository`() {
        interactor.setNotificationsEnabled(true)

        verify { settingsRepository.setEngagementRemindersEnabled(true) }
    }

    @Test
    fun `setNotificationsEnabled delegates false to repository`() {
        interactor.setNotificationsEnabled(false)

        verify { settingsRepository.setEngagementRemindersEnabled(false) }
    }

    // endregion
}
