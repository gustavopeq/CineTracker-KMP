package features.settings.domain

import app.cash.turbine.test
import common.util.platform.PlatformUtils
import database.repository.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
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
        every { PlatformUtils.applyAppLocale(any()) } just runs
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
        every { PlatformUtils.getLocale() } returns "pt-BR"

        val result = interactor.getAppLanguage()

        assertEquals("pt-BR", result)
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
        every { settingsRepository.getAppLanguage() } returns "en-US"

        interactor.setAppLanguage("es-ES")

        verify { settingsRepository.setAppLanguage("es-ES") }
    }

    @Test
    fun `setAppLanguage applies locale when language changes`() {
        every { settingsRepository.getAppLanguage() } returns "en-US"

        interactor.setAppLanguage("pt-BR")

        verify { PlatformUtils.applyAppLocale("pt-BR") }
    }

    @Test
    fun `setAppLanguage skips locale application when language unchanged`() {
        every { settingsRepository.getAppLanguage() } returns "pt-BR"

        interactor.setAppLanguage("pt-BR")

        verify(exactly = 0) { PlatformUtils.applyAppLocale(any()) }
    }

    @Test
    fun `setAppLanguage emits settingsChanged when language changes`() = runTest {
        every { settingsRepository.getAppLanguage() } returns "en-US"

        interactor.settingsChanged.test {
            interactor.setAppLanguage("pt-BR")
            awaitItem()
        }
    }

    @Test
    fun `setAppLanguage does not emit settingsChanged when language unchanged`() = runTest {
        every { settingsRepository.getAppLanguage() } returns "pt-BR"

        interactor.settingsChanged.test {
            interactor.setAppLanguage("pt-BR")
            expectNoEvents()
        }
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
        every { settingsRepository.getAppRegion() } returns "US"
        every { PlatformUtils.getUserCountry() } returns "US"

        interactor.setAppRegion("BR")

        verify { settingsRepository.setAppRegion("BR") }
    }

    @Test
    fun `setAppRegion emits settingsChanged when region changes`() = runTest {
        every { settingsRepository.getAppRegion() } returns "US"
        every { PlatformUtils.getUserCountry() } returns "US"

        interactor.settingsChanged.test {
            interactor.setAppRegion("BR")
            awaitItem()
        }
    }

    @Test
    fun `setAppRegion does not emit settingsChanged when region unchanged`() = runTest {
        every { settingsRepository.getAppRegion() } returns "BR"
        every { PlatformUtils.getUserCountry() } returns "BR"

        interactor.settingsChanged.test {
            interactor.setAppRegion("BR")
            expectNoEvents()
        }
    }

    // endregion

    // region getSupportedLanguages

    @Test
    fun `getSupportedLanguages returns 3 entries`() {
        val languages = interactor.getSupportedLanguages()

        assertEquals(3, languages.size)
    }

    @Test
    fun `getSupportedLanguages contains en-US with correct display name`() {
        val languages = interactor.getSupportedLanguages()
        val enUS = languages.find { it.tag == "en-US" }

        assertEquals("English", enUS?.displayName)
    }

    @Test
    fun `getSupportedLanguages contains pt-BR with correct display name`() {
        val languages = interactor.getSupportedLanguages()
        val ptBR = languages.find { it.tag == "pt-BR" }

        assertEquals("Portugu\u00eas", ptBR?.displayName)
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
