package features.details.ui

import common.domain.models.content.DetailedContent
import common.domain.models.content.GenericContent
import common.domain.models.content.Videos
import common.domain.models.list.ListItem
import common.domain.models.person.PersonImage
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import database.repository.SettingsRepository
import features.details.domain.DetailsInteractor
import features.details.events.DetailsEvents
import features.details.state.DetailsState
import features.watchlist.ui.model.DefaultLists
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

/**
 * Waits for IO-dispatched coroutines launched by the ViewModel to complete.
 * Necessary because viewModelScope.launch(Dispatchers.IO) runs on real threads
 * that are NOT controlled by StandardTestDispatcher.
 */
private fun awaitIO(ms: Long = 300L) = Thread.sleep(ms)

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val detailsInteractor: DetailsInteractor = mockk()
    private val settingsRepository: SettingsRepository = mockk(relaxUnitFun = true)
    private val testDispatcher = StandardTestDispatcher()

    private val personalRatingFlow = MutableStateFlow<Float?>(null)
    private val contentInListStatusFlow = MutableStateFlow(
        mapOf(
            DefaultLists.WATCHLIST.listId to false,
            DefaultLists.WATCHED.listId to false
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Default stubs so init block never throws
        every { detailsInteractor.getAllLists() } returns flowOf(
            listOf(
                ListItem(DefaultLists.WATCHLIST.listId, "Watchlist"),
                ListItem(DefaultLists.WATCHED.listId, "Watched")
            )
        )
        personalRatingFlow.value = null
        every { detailsInteractor.getPersonalRating(any()) } returns personalRatingFlow
        contentInListStatusFlow.value = mapOf(
            DefaultLists.WATCHLIST.listId to false,
            DefaultLists.WATCHED.listId to false
        )
        every { detailsInteractor.verifyContentInLists(any(), any()) } returns contentInListStatusFlow

        // Default: overlay not yet seen
        every { settingsRepository.hasSeenDetailsOverlay() } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(contentId: Int = 1, mediaType: MediaType = MediaType.MOVIE): DetailsViewModel =
        DetailsViewModel(
            contentId = contentId,
            mediaType = mediaType,
            detailsInteractor = detailsInteractor,
            settingsRepository = settingsRepository
        )

    private fun fakeDetailedContent(id: Int = 1, name: String = "Test Movie", mediaType: MediaType = MediaType.MOVIE) =
        DetailedContent(
            id = id,
            name = name,
            rating = 7.5,
            overview = "Overview",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            mediaType = mediaType
        )

    private fun stubSuccessfulMovieDetails(
        contentId: Int = 1,
        detailsState: DetailsState = DetailsState().apply {
            detailsInfo.value = fakeDetailedContent(id = contentId)
        },
        castState: DetailsState = DetailsState()
    ) {
        coEvery { detailsInteractor.getContentDetailsById(contentId, MediaType.MOVIE) } returns detailsState
        coEvery { detailsInteractor.getContentCastById(contentId, MediaType.MOVIE) } returns castState
        coEvery { detailsInteractor.getStreamingProviders(contentId, MediaType.MOVIE) } returns emptyList()
        coEvery { detailsInteractor.getContentVideosById(contentId, MediaType.MOVIE) } returns emptyList()
        coEvery { detailsInteractor.getRecommendationsContentById(contentId, MediaType.MOVIE) } returns emptyList()
        coEvery { detailsInteractor.updateCachedFields(any(), any(), any(), any(), any()) } returns Unit
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `loadState starts as Loading before coroutines run`() {
        stubSuccessfulMovieDetails()
        val viewModel = createViewModel()
        assertEquals(DataLoadStatus.Loading, viewModel.loadState.value)
    }

    @Test
    fun `personalRating is populated from interactor after init`() = runTest {
        personalRatingFlow.value = 8.0f
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        assertEquals(8.0f, viewModel.personalRating.value)
    }

    @Test
    fun `personalRating is null when interactor returns null`() = runTest {
        personalRatingFlow.value = null
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        assertNull(viewModel.personalRating.value)
    }

    // ── FetchDetails — success ────────────────────────────────────────────────

    @Test
    fun `loadState transitions to Success after successful details and cast fetch`() = runTest {
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DataLoadStatus.Success, viewModel.loadState.value)
    }

    @Test
    fun `contentDetails is populated after successful fetch`() = runTest {
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.contentDetails.value)
        assertEquals("Test Movie", viewModel.contentDetails.value?.name)
    }

    @Test
    fun `contentVideos is populated for MOVIE after successful fetch`() = runTest {
        val detailsState = DetailsState().apply {
            detailsInfo.value = fakeDetailedContent(id = 1, name = "Movie")
        }
        coEvery { detailsInteractor.getContentDetailsById(1, MediaType.MOVIE) } returns detailsState
        coEvery { detailsInteractor.getContentCastById(1, MediaType.MOVIE) } returns DetailsState()
        coEvery { detailsInteractor.getStreamingProviders(1, MediaType.MOVIE) } returns emptyList()
        coEvery { detailsInteractor.getContentVideosById(1, MediaType.MOVIE) } returns listOf(
            Videos(key = "abc", name = "Trailer", publishedAt = "2024-01-01")
        )
        coEvery { detailsInteractor.getRecommendationsContentById(1, MediaType.MOVIE) } returns emptyList()
        coEvery { detailsInteractor.updateCachedFields(any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.contentVideos.value.size)
    }

    @Test
    fun `personCredits and personImages populated for PERSON after successful fetch`() = runTest {
        val personDetailsState = DetailsState().apply {
            detailsInfo.value = fakeDetailedContent(id = 1, name = "Test Person", mediaType = MediaType.PERSON)
        }
        coEvery { detailsInteractor.getContentDetailsById(1, MediaType.PERSON) } returns personDetailsState
        coEvery { detailsInteractor.getContentCastById(1, MediaType.PERSON) } returns DetailsState()
        coEvery { detailsInteractor.getStreamingProviders(1, MediaType.PERSON) } returns emptyList()
        coEvery { detailsInteractor.getPersonCreditsById(1) } returns listOf(
            GenericContent(
                id = 10,
                name = "Movie A",
                rating = 7.0,
                overview = "",
                posterPath = "/p.jpg",
                backdropPath = "",
                mediaType = MediaType.MOVIE
            )
        )
        coEvery { detailsInteractor.getPersonImages(1) } returns listOf(
            PersonImage(aspectRatio = 1.0, filePath = "/img.jpg", height = 100, width = 100)
        )
        coEvery { detailsInteractor.updateCachedFields(any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel(mediaType = MediaType.PERSON)
        advanceUntilIdle()

        assertEquals(1, viewModel.personCredits.value.size)
        assertEquals(1, viewModel.personImages.value.size)
    }

    @Test
    fun `fetchAdditionalInfo is not called when loadState is Failed`() = runTest {
        val failedState = DetailsState().apply { setError("404") }
        coEvery { detailsInteractor.getContentDetailsById(1, MediaType.MOVIE) } returns failedState
        coEvery { detailsInteractor.getContentCastById(any(), any()) } returns DetailsState()
        coEvery { detailsInteractor.getStreamingProviders(any(), any()) } returns emptyList()

        createViewModel()
        advanceUntilIdle()

        coVerify(exactly = 0) { detailsInteractor.getContentVideosById(any(), any()) }
        coVerify(exactly = 0) { detailsInteractor.getRecommendationsContentById(any(), any()) }
    }

    // ── FetchDetails — failure ────────────────────────────────────────────────

    @Test
    fun `loadState is Failed when getContentDetailsById returns error state`() = runTest {
        val failedState = DetailsState().apply { setError("404") }
        coEvery { detailsInteractor.getContentDetailsById(1, MediaType.MOVIE) } returns failedState
        coEvery { detailsInteractor.getStreamingProviders(any(), any()) } returns emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DataLoadStatus.Failed, viewModel.loadState.value)
    }

    @Test
    fun `loadState is Failed when getContentCastById returns error state`() = runTest {
        val failedCastState = DetailsState().apply { setError("503") }
        coEvery { detailsInteractor.getContentDetailsById(1, MediaType.MOVIE) } returns DetailsState().apply {
            detailsInfo.value = fakeDetailedContent()
        }
        coEvery { detailsInteractor.getContentCastById(1, MediaType.MOVIE) } returns failedCastState
        coEvery { detailsInteractor.getStreamingProviders(any(), any()) } returns emptyList()
        coEvery { detailsInteractor.updateCachedFields(any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DataLoadStatus.Failed, viewModel.loadState.value)
    }

    // ── ToggleContentFromList ─────────────────────────────────────────────────

    @Test
    fun `ToggleContentFromList calls toggleWatchlist with correct args`() = runTest {
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailsEvents.ToggleContentFromList(DefaultLists.WATCHLIST.listId))
        awaitIO()

        coVerify {
            detailsInteractor.toggleWatchlist(
                currentStatus = false,
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = DefaultLists.WATCHLIST.listId,
                title = any(),
                posterPath = any(),
                voteAverage = any()
            )
        }
    }

    @Test
    fun `ToggleContentFromList flips contentInListStatus from false to true`() = runTest {
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.contentInListStatus.value[DefaultLists.WATCHLIST.listId] == true)

        viewModel.onEvent(DetailsEvents.ToggleContentFromList(DefaultLists.WATCHLIST.listId))
        // Simulate the Flow emitting new status after the toggle
        contentInListStatusFlow.value = mapOf(
            DefaultLists.WATCHLIST.listId to true,
            DefaultLists.WATCHED.listId to false
        )
        awaitIO()

        assertTrue(viewModel.contentInListStatus.value[DefaultLists.WATCHLIST.listId] == true)
    }

    @Test
    fun `ToggleContentFromList sets snackbarState addedItem to true when adding`() = runTest {
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailsEvents.ToggleContentFromList(DefaultLists.WATCHLIST.listId))
        awaitIO()

        assertTrue(viewModel.snackbarState.value.addedItem)
        assertTrue(viewModel.snackbarState.value.displaySnackbar.value)
    }

    @Test
    fun `ToggleContentFromList sets snackbarState addedItem to false when removing`() = runTest {
        // Start with item already in list
        contentInListStatusFlow.value = mapOf(
            DefaultLists.WATCHLIST.listId to true,
            DefaultLists.WATCHED.listId to false
        )
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.onEvent(DetailsEvents.ToggleContentFromList(DefaultLists.WATCHLIST.listId))
        awaitIO()

        assertFalse(viewModel.snackbarState.value.addedItem)
    }

    // ── OnError ───────────────────────────────────────────────────────────────

    @Test
    fun `OnError resets loadState to Loading and sets detailsFailedLoading true`() = runTest {
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailsEvents.OnError)

        assertEquals(DataLoadStatus.Loading, viewModel.loadState.value)
        assertTrue(viewModel.detailsFailedLoading.value)
    }

    @Test
    fun `FetchDetails after OnError re-calls getContentDetailsById`() = runTest {
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailsEvents.OnError)
        viewModel.onEvent(DetailsEvents.FetchDetails)
        advanceUntilIdle()

        coVerify(atLeast = 2) { detailsInteractor.getContentDetailsById(1, MediaType.MOVIE) }
    }

    // ── OnSnackbarDismiss ─────────────────────────────────────────────────────

    @Test
    fun `OnSnackbarDismiss sets snackbar displaySnackbar to false`() = runTest {
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        // Show snackbar first — set via IO thread, so wait for it
        viewModel.onEvent(DetailsEvents.ToggleContentFromList(DefaultLists.WATCHLIST.listId))
        awaitIO()
        assertTrue(viewModel.snackbarState.value.displaySnackbar.value)

        // Dismiss is synchronous
        viewModel.onEvent(DetailsEvents.OnSnackbarDismiss)

        assertFalse(viewModel.snackbarState.value.displaySnackbar.value)
    }

    // ── setPersonalRating ─────────────────────────────────────────────────────

    @Test
    fun `setPersonalRating updates personalRating and calls interactor`() = runTest {
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.setPersonalRating(any(), any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.setPersonalRating(9.0f)
        // Simulate the Flow emitting the new rating after the mutation
        personalRatingFlow.value = 9.0f
        awaitIO()

        assertEquals(9.0f, viewModel.personalRating.value)
        coVerify { detailsInteractor.setPersonalRating(1, MediaType.MOVIE, 9.0f) }
    }

    // ── removePersonalRating ──────────────────────────────────────────────────

    @Test
    fun `removePersonalRating sets personalRating to null and calls interactor`() = runTest {
        personalRatingFlow.value = 7.0f
        stubSuccessfulMovieDetails()
        coEvery { detailsInteractor.removePersonalRating(any(), any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.removePersonalRating()
        // Simulate the Flow emitting null after the mutation
        personalRatingFlow.value = null
        awaitIO()

        assertNull(viewModel.personalRating.value)
        coVerify { detailsInteractor.removePersonalRating(1, MediaType.MOVIE) }
    }

    // ── Details Onboarding Overlay ───────────────────────────────────────────

    @Test
    fun `showDetailsOverlay is true after successful movie load when not yet seen`() = runTest {
        every { settingsRepository.hasSeenDetailsOverlay() } returns false
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(true, viewModel.showDetailsOverlay.value)
    }

    @Test
    fun `showDetailsOverlay is false when overlay was already seen`() = runTest {
        every { settingsRepository.hasSeenDetailsOverlay() } returns true
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(false, viewModel.showDetailsOverlay.value)
    }

    @Test
    fun `showDetailsOverlay is false for PERSON media type`() = runTest {
        every { settingsRepository.hasSeenDetailsOverlay() } returns false
        val personDetailsState = DetailsState().apply {
            detailsInfo.value = fakeDetailedContent(id = 1, name = "Test Person", mediaType = MediaType.PERSON)
        }
        coEvery { detailsInteractor.getContentDetailsById(1, MediaType.PERSON) } returns personDetailsState
        coEvery { detailsInteractor.getContentCastById(1, MediaType.PERSON) } returns DetailsState()
        coEvery { detailsInteractor.getStreamingProviders(1, MediaType.PERSON) } returns emptyList()
        coEvery { detailsInteractor.getPersonCreditsById(1) } returns emptyList()
        coEvery { detailsInteractor.getPersonImages(1) } returns emptyList()
        coEvery { detailsInteractor.updateCachedFields(any(), any(), any(), any(), any()) } returns Unit

        val viewModel = createViewModel(mediaType = MediaType.PERSON)
        advanceUntilIdle()

        assertEquals(false, viewModel.showDetailsOverlay.value)
    }

    @Test
    fun `DismissDetailsOverlay sets overlay to false and persists to settings`() = runTest {
        every { settingsRepository.hasSeenDetailsOverlay() } returns false
        stubSuccessfulMovieDetails()

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(true, viewModel.showDetailsOverlay.value)

        viewModel.onEvent(DetailsEvents.DismissDetailsOverlay)

        assertEquals(false, viewModel.showDetailsOverlay.value)
        verify { settingsRepository.setDetailsOverlaySeen() }
    }
}
