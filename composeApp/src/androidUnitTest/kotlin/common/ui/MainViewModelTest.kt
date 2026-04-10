package common.ui

import androidx.compose.ui.text.input.TextFieldValue
import common.domain.models.util.MediaType
import common.domain.models.util.SortTypeItem
import auth.model.AuthState
import auth.repository.AuthRepository
import common.util.platform.AppNotifications
import database.repository.DatabaseRepository
import database.repository.SettingsRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val databaseRepository: DatabaseRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk {
        every { hasCompletedOnboarding() } returns true
        every { areEngagementRemindersEnabled() } returns false
    }
    private val authRepository: AuthRepository = mockk(relaxUnitFun = true)
    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { authRepository.authState } returns authStateFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = MainViewModel(databaseRepository, settingsRepository, authRepository)

    // ── updateSortType ────────────────────────────────────────────────────────

    @Test
    fun `updateSortType sets movieSortType when media type is MOVIE`() {
        val viewModel = createViewModel()
        // default is MOVIE
        viewModel.updateSortType(SortTypeItem.TopRated)

        assertIs<SortTypeItem.TopRated>(viewModel.movieSortType.value)
    }

    @Test
    fun `updateSortType sets showSortType when media type is SHOW`() {
        val viewModel = createViewModel()
        viewModel.updateMediaType(MediaType.SHOW)
        viewModel.updateSortType(SortTypeItem.ShowTopRated)

        assertIs<SortTypeItem.ShowTopRated>(viewModel.showSortType.value)
    }

    @Test
    fun `updateSortType does nothing for PERSON media type`() {
        val viewModel = createViewModel()
        viewModel.updateMediaType(MediaType.PERSON)
        viewModel.updateSortType(SortTypeItem.Popular)

        // should remain at defaults
        assertIs<SortTypeItem.NowPlaying>(viewModel.movieSortType.value)
        assertIs<SortTypeItem.AiringToday>(viewModel.showSortType.value)
    }

    // ── createNewList ─────────────────────────────────────────────────────────

    @Test
    fun `createNewList calls closeSheet when list created`() = runTest {
        coEvery { databaseRepository.addNewList(any()) } returns true
        val viewModel = createViewModel()
        var sheetClosed = false

        viewModel.createNewList { sheetClosed = true }

        assertTrue(sheetClosed)
        assertFalse(viewModel.isDuplicatedListName.value)
    }

    @Test
    fun `createNewList sets isDuplicatedListName when list not created`() = runTest {
        coEvery { databaseRepository.addNewList(any()) } returns false
        val viewModel = createViewModel()
        var sheetClosed = false

        viewModel.createNewList { sheetClosed = true }

        assertFalse(sheetClosed)
        assertTrue(viewModel.isDuplicatedListName.value)
    }

    // ── updateDisplayCreateNewList ────────────────────────────────────────────

    @Test
    fun `updateDisplayCreateNewList resets text field and duplicate flag`() {
        val viewModel = createViewModel()
        viewModel.updateCreateNewListTextField(TextFieldValue("something"))

        viewModel.updateDisplayCreateNewList(true)

        assertEquals("", viewModel.newListTextFieldValue.value.text)
        assertFalse(viewModel.isDuplicatedListName.value)
        assertTrue(viewModel.displayCreateNewList.value)
    }

    // ── updateCreateNewListTextField ──────────────────────────────────────────

    @Test
    fun `updateCreateNewListTextField clears duplicate flag when true`() = runTest {
        coEvery { databaseRepository.addNewList(any()) } returns false
        val viewModel = createViewModel()
        viewModel.createNewList {} // sets isDuplicatedListName = true

        assertTrue(viewModel.isDuplicatedListName.value)

        viewModel.updateCreateNewListTextField(TextFieldValue("new name"))

        assertFalse(viewModel.isDuplicatedListName.value)
        assertEquals("new name", viewModel.newListTextFieldValue.value.text)
    }

    // ── shouldShowNotificationDialog ──────────────────────────────────────────────

    @Test
    fun `shouldShowNotificationDialog is true when reminders are not enabled`() {
        every { settingsRepository.areEngagementRemindersEnabled() } returns false
        val viewModel = createViewModel()

        assertTrue(viewModel.shouldShowNotificationDialog.value)
    }

    @Test
    fun `shouldShowNotificationDialog is false when reminders are already enabled`() {
        every { settingsRepository.areEngagementRemindersEnabled() } returns true
        val viewModel = createViewModel()

        assertFalse(viewModel.shouldShowNotificationDialog.value)
    }

    @Test
    fun `skipEngagementReminders dismisses the dialog`() {
        every { settingsRepository.areEngagementRemindersEnabled() } returns false
        val viewModel = createViewModel()

        viewModel.skipEngagementReminders()

        assertFalse(viewModel.shouldShowNotificationDialog.value)
    }

    @Test
    fun `enableEngagementReminders saves preference, schedules reminders, and dismisses dialog`() {
        mockkObject(AppNotifications)
        every { AppNotifications.scheduleEngagementReminders() } just runs
        every { settingsRepository.areEngagementRemindersEnabled() } returns false
        every { settingsRepository.setEngagementRemindersEnabled(any()) } just runs
        val viewModel = createViewModel()

        viewModel.enableEngagementReminders()

        verify { settingsRepository.setEngagementRemindersEnabled(true) }
        verify { AppNotifications.scheduleEngagementReminders() }
        assertFalse(viewModel.shouldShowNotificationDialog.value)
    }

    @Test
    fun `init fetches preferences when user is logged in`() = runTest {
        authStateFlow.value = AuthState.LoggedIn(userId = "user-123", displayName = "Test")

        createViewModel()
        advanceUntilIdle()

        coVerify { authRepository.fetchAndApplyPreferences() }
    }

    @Test
    fun `init does not fetch preferences when user is logged out`() = runTest {
        authStateFlow.value = AuthState.LoggedOut

        createViewModel()
        advanceUntilIdle()

        coVerify(exactly = 0) { authRepository.fetchAndApplyPreferences() }
    }
}
