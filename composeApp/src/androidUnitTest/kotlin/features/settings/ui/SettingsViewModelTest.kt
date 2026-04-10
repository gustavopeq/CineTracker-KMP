package features.settings.ui

import auth.model.AuthState
import auth.repository.AuthRepository
import auth.service.AuthResult
import common.util.platform.AppNotifications
import database.model.ListEntity
import database.repository.DatabaseRepository
import features.settings.domain.LanguageItem
import features.settings.domain.RegionItem
import features.settings.domain.SettingsInteractor
import features.settings.events.SettingsEvent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val settingsInteractor: SettingsInteractor = mockk(relaxUnitFun = true)
    private val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    private val databaseRepository: DatabaseRepository = mockk(relaxUnitFun = true)
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.LoggedOut)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        mockkObject(AppNotifications)
        every { AppNotifications.scheduleEngagementReminders() } returns Unit
        every { AppNotifications.cancelEngagementReminders() } returns Unit
        every { authRepository.authState } returns authStateFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun setupDefaultMocks(
        language: String = "en-US",
        region: String = "US",
        notificationsEnabled: Boolean = false,
        settingsChangedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    ) {
        every { settingsInteractor.getAppLanguage() } returns language
        every { settingsInteractor.getAppRegion() } returns region
        every { settingsInteractor.areNotificationsEnabled() } returns notificationsEnabled
        every { settingsInteractor.settingsChanged } returns settingsChangedFlow
        every { settingsInteractor.getSupportedLanguages() } returns listOf(
            LanguageItem("en-US", "English"),
            LanguageItem("pt-BR", "Portugu\u00eas"),
            LanguageItem("es-ES", "Espa\u00f1ol")
        )
        every { settingsInteractor.getSupportedRegions() } returns listOf(
            RegionItem("US", "United States"),
            RegionItem("BR", "Brazil"),
            RegionItem("ES", "Spain")
        )
    }

    private fun createViewModel(): SettingsViewModel = SettingsViewModel(
        settingsInteractor,
        authRepository,
        databaseRepository
    )

    @Test
    fun `init loads current language display name`() {
        setupDefaultMocks(language = "pt-BR")

        val viewModel = createViewModel()

        assertEquals("Portugu\u00eas", viewModel.currentLanguageDisplay.value)
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
    fun `NotificationPermissionResult granted enables notifications and schedules reminders`() {
        setupDefaultMocks()
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.NotificationPermissionResult(granted = true))

        verify { settingsInteractor.setNotificationsEnabled(true) }
        verify { AppNotifications.scheduleEngagementReminders() }
        assertTrue(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `DisableNotifications disables notifications and cancels reminders`() {
        setupDefaultMocks(notificationsEnabled = true)
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.DisableNotifications)

        verify { settingsInteractor.setNotificationsEnabled(false) }
        verify { AppNotifications.cancelEngagementReminders() }
        assertFalse(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `NotificationPermissionResult denied does not enable notifications`() {
        setupDefaultMocks()
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.NotificationPermissionResult(granted = false))

        verify(exactly = 0) { settingsInteractor.setNotificationsEnabled(any()) }
        assertFalse(viewModel.notificationsEnabled.value)
    }

    @Test
    fun `settingsChanged emission triggers auto-refresh of language display`() = runTest {
        val settingsChangedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        setupDefaultMocks(language = "en-US", region = "US", settingsChangedFlow = settingsChangedFlow)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("English", viewModel.currentLanguageDisplay.value)

        every { settingsInteractor.getAppLanguage() } returns "pt-BR"
        settingsChangedFlow.emit(Unit)
        advanceUntilIdle()

        assertEquals("Portugu\u00eas", viewModel.currentLanguageDisplay.value)
    }

    @Test
    fun `settingsChanged emission triggers auto-refresh of region display`() = runTest {
        val settingsChangedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        setupDefaultMocks(language = "en-US", region = "US", settingsChangedFlow = settingsChangedFlow)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("United States", viewModel.currentRegionDisplay.value)

        every { settingsInteractor.getAppRegion() } returns "BR"
        settingsChangedFlow.emit(Unit)
        advanceUntilIdle()

        assertEquals("Brazil", viewModel.currentRegionDisplay.value)
    }

    @Test
    fun `DeleteAccount with removeData clears default lists and deletes custom lists`() = runTest {
        setupDefaultMocks()
        coEvery { authRepository.deleteAccount() } returns AuthResult.Success(Unit)
        coEvery { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                ListEntity(listId = 1, listName = "Watchlist", isDefault = true),
                ListEntity(listId = 2, listName = "Watched", isDefault = true),
                ListEntity(listId = 3, listName = "Horror", isDefault = false),
                ListEntity(listId = 4, listName = "Comedy", isDefault = false)
            )
        )
        coEvery { databaseRepository.clearList(any()) } returns Unit
        coEvery { databaseRepository.deleteList(any()) } returns Unit
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.DeleteAccount(keepLocalData = false))
        advanceUntilIdle()

        coVerify(exactly = 1) { databaseRepository.clearList(1) }
        coVerify(exactly = 1) { databaseRepository.clearList(2) }
        coVerify(exactly = 0) { databaseRepository.deleteList(1) }
        coVerify(exactly = 0) { databaseRepository.deleteList(2) }
        coVerify(exactly = 1) { databaseRepository.deleteList(3) }
        coVerify(exactly = 1) { databaseRepository.deleteList(4) }
    }

    @Test
    fun `DeleteAccount with removeData clears but never deletes default lists`() = runTest {
        setupDefaultMocks()
        coEvery { authRepository.deleteAccount() } returns AuthResult.Success(Unit)
        coEvery { databaseRepository.getAllLists() } returns flowOf(
            listOf(
                ListEntity(listId = 1, listName = "Watchlist", isDefault = true),
                ListEntity(listId = 2, listName = "Watched", isDefault = true)
            )
        )
        coEvery { databaseRepository.clearList(any()) } returns Unit
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.DeleteAccount(keepLocalData = false))
        advanceUntilIdle()

        coVerify(exactly = 1) { databaseRepository.clearList(1) }
        coVerify(exactly = 1) { databaseRepository.clearList(2) }
        coVerify(exactly = 0) { databaseRepository.deleteList(any()) }
    }

    @Test
    fun `DeleteAccount with keepData does not touch any lists`() = runTest {
        setupDefaultMocks()
        coEvery { authRepository.deleteAccount() } returns AuthResult.Success(Unit)
        val viewModel = createViewModel()

        viewModel.onEvent(SettingsEvent.DeleteAccount(keepLocalData = true))
        advanceUntilIdle()

        coVerify(exactly = 0) { databaseRepository.getAllLists() }
        coVerify(exactly = 0) { databaseRepository.clearList(any()) }
        coVerify(exactly = 0) { databaseRepository.deleteList(any()) }
    }
}
