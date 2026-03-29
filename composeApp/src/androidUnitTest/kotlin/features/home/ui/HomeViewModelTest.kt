package features.home.ui

import common.domain.models.list.ListItem
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import database.backfill.CachedFieldsBackfill
import features.home.domain.HomeInteractor
import features.home.events.HomeEvent
import features.home.ui.state.HomeState
import features.home.util.fakeHomeState
import features.watchlist.domain.ListInteractor
import features.watchlist.util.fakeGenericContent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Waits for Dispatchers.IO coroutines launched by the ViewModel to complete.
 * loadWatchlist() uses viewModelScope.launch(Dispatchers.IO) which runs on real threads.
 */
private fun awaitIO(ms: Long = 300L) = Thread.sleep(ms)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val homeInteractor: HomeInteractor = mockk()
    private val listInteractor: ListInteractor = mockk()
    private val cachedFieldsBackfill: CachedFieldsBackfill = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Default stubs so init block never throws
        coEvery { homeInteractor.getTrendingMulti() } returns fakeHomeState(fakeGenericContent())
        coEvery { homeInteractor.getTrendingPerson() } returns emptyList()
        coEvery { homeInteractor.getMoviesComingSoon() } returns emptyList()
        every { homeInteractor.getWatchlistFlow() } returns flowOf(emptyList())
        every { listInteractor.getAllLists() } returns flowOf(emptyList())
        every { listInteractor.verifyContentInLists(any(), any()) } returns flowOf(emptyMap())
        coEvery { cachedFieldsBackfill.backfillIfNeeded() } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = HomeViewModel(homeInteractor, listInteractor, cachedFieldsBackfill)

    // ── Init ──────────────────────────────────────────────────────────────────

    @Test
    fun `loadState starts as Loading before coroutines run`() {
        val viewModel = createViewModel()
        assertEquals(DataLoadStatus.Loading, viewModel.loadState.value)
        awaitIO() // allow init coroutine to finish before tearDown
    }

    @Test
    fun `loadState transitions to Success and trendingMulti is populated after init`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DataLoadStatus.Success, viewModel.loadState.value)
        assertTrue(viewModel.trendingMulti.value.isNotEmpty())
    }

    // ── loadHomeScreen — failure ──────────────────────────────────────────────

    @Test
    fun `loadState is Failed when getTrendingMulti returns error state`() = runTest {
        val errorState = HomeState().apply { setError("500") }
        coEvery { homeInteractor.getTrendingMulti() } returns errorState

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DataLoadStatus.Failed, viewModel.loadState.value)
    }

    @Test
    fun `getTrendingPerson and getMoviesComingSoon are not called when getTrendingMulti fails`() = runTest {
        val errorState = HomeState().apply { setError("500") }
        coEvery { homeInteractor.getTrendingMulti() } returns errorState

        createViewModel()
        advanceUntilIdle()

        coVerify(exactly = 0) { homeInteractor.getTrendingPerson() }
        coVerify(exactly = 0) { homeInteractor.getMoviesComingSoon() }
    }

    // ── State population after successful init ────────────────────────────────

    @Test
    fun `trendingPerson is populated after successful init`() = runTest {
        coEvery { homeInteractor.getTrendingPerson() } returns listOf(
            common.domain.models.person.PersonDetails(
                id = 1, title = "Actor", overview = "", posterPath = "/p.jpg",
                mediaType = MediaType.PERSON,
                birthday = null, deathday = null, placeOfBirth = null,
                knownForDepartment = null, knownFor = emptyList()
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.trendingPerson.value.size)
    }

    @Test
    fun `moviesComingSoon is populated after successful init`() = runTest {
        coEvery { homeInteractor.getMoviesComingSoon() } returns listOf(fakeGenericContent(id = 99))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.moviesComingSoon.value.size)
        assertEquals(99, viewModel.moviesComingSoon.value[0].id)
    }

    @Test
    fun `myWatchlist is populated after init`() = runTest {
        every { homeInteractor.getWatchlistFlow() } returns flowOf(listOf(fakeGenericContent(id = 5)))

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        assertEquals(1, viewModel.myWatchlist.value.size)
        assertEquals(5, viewModel.myWatchlist.value[0].id)
    }

    // ── LoadHome event ────────────────────────────────────────────────────────

    @Test
    fun `LoadHome event triggers getTrendingMulti again`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeEvent.LoadHome)
        advanceUntilIdle()

        coVerify(atLeast = 2) { homeInteractor.getTrendingMulti() }
    }

    // ── OnError event ─────────────────────────────────────────────────────────

    @Test
    fun `OnError resets loadState to Loading`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(DataLoadStatus.Success, viewModel.loadState.value)

        viewModel.onEvent(HomeEvent.OnError)

        assertEquals(DataLoadStatus.Loading, viewModel.loadState.value)
    }

    @Test
    fun `OnError clears trendingMulti trendingPerson and moviesComingSoon`() = runTest {
        coEvery { homeInteractor.getTrendingPerson() } returns listOf(
            common.domain.models.person.PersonDetails(
                id = 1, title = "Actor", overview = "", posterPath = "/p.jpg",
                mediaType = MediaType.PERSON,
                birthday = null, deathday = null, placeOfBirth = null,
                knownForDepartment = null, knownFor = emptyList()
            )
        )
        coEvery { homeInteractor.getMoviesComingSoon() } returns listOf(fakeGenericContent(id = 99))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeEvent.OnError)

        assertTrue(viewModel.trendingMulti.value.isEmpty())
        assertTrue(viewModel.trendingPerson.value.isEmpty())
        assertTrue(viewModel.moviesComingSoon.value.isEmpty())
    }

    @Test
    fun `OnError clears featuredContentInListStatus`() = runTest {
        every {
            listInteractor.verifyContentInLists(any(), any())
        } returns flowOf(mapOf(1 to true))

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.onEvent(HomeEvent.OnError)

        assertTrue(viewModel.featuredContentInListStatus.value.isEmpty())
    }

    // ── Featured list status ─────────────────────────────────────────────────

    @Test
    fun `loadFeaturedListStatus sets status map for featured content`() = runTest {
        val statusMap = mapOf(1 to true, 2 to false)
        every {
            listInteractor.verifyContentInLists(1, MediaType.MOVIE)
        } returns flowOf(statusMap)

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        assertEquals(statusMap, viewModel.featuredContentInListStatus.value)
    }

    @Test
    fun `featuredContentInListStatus is empty when no featured content`() = runTest {
        coEvery { homeInteractor.getTrendingMulti() } returns fakeHomeState()

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        assertTrue(viewModel.featuredContentInListStatus.value.isEmpty())
    }

    @Test
    fun `allLists is populated after init`() = runTest {
        val lists = listOf(
            ListItem(id = 1, name = "watchlist", isDefault = true),
            ListItem(id = 2, name = "watched", isDefault = true)
        )
        every { listInteractor.getAllLists() } returns flowOf(lists)

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        assertEquals(lists, viewModel.allLists.value)
    }

    // ── Toggle featured from list ────────────────────────────────────────────

    @Test
    fun `toggleFeaturedFromList adds content to list and updates status`() = runTest {
        every {
            listInteractor.verifyContentInLists(1, MediaType.MOVIE)
        } returns flowOf(mapOf(1 to false, 2 to false))
        coEvery {
            listInteractor.toggleWatchlist(false, 1, MediaType.MOVIE, 1, any(), any(), any())
        } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.onEvent(HomeEvent.ToggleFeaturedFromList(listId = 1))
        awaitIO()

        coVerify { listInteractor.toggleWatchlist(false, 1, MediaType.MOVIE, 1, any(), any(), any()) }
    }

    @Test
    fun `toggleFeaturedFromList removes content from list and updates status`() = runTest {
        every {
            listInteractor.verifyContentInLists(1, MediaType.MOVIE)
        } returns flowOf(mapOf(1 to true, 2 to false))
        coEvery {
            listInteractor.toggleWatchlist(true, 1, MediaType.MOVIE, 1, any(), any(), any())
        } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.onEvent(HomeEvent.ToggleFeaturedFromList(listId = 1))
        awaitIO()

        coVerify { listInteractor.toggleWatchlist(true, 1, MediaType.MOVIE, 1, any(), any(), any()) }
    }

    @Test
    fun `toggleFeaturedFromList does nothing when no featured content`() = runTest {
        coEvery { homeInteractor.getTrendingMulti() } returns fakeHomeState()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeEvent.ToggleFeaturedFromList(listId = 1))
        awaitIO()

        coVerify(exactly = 0) { listInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any()) }
    }

    // ── Bottom sheet visibility ──────────────────────────────────────────────

    @Test
    fun `OpenListBottomSheet sets showListBottomSheet to true`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.showListBottomSheet.value)

        viewModel.onEvent(HomeEvent.OpenListBottomSheet)

        assertTrue(viewModel.showListBottomSheet.value)
    }

    @Test
    fun `CloseListBottomSheet sets showListBottomSheet to false`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(HomeEvent.OpenListBottomSheet)
        assertTrue(viewModel.showListBottomSheet.value)

        viewModel.onEvent(HomeEvent.CloseListBottomSheet)
        assertFalse(viewModel.showListBottomSheet.value)
    }

    // ── toggleFeaturedFromList — cached field forwarding ─────────────────────

    @Test
    fun `toggleFeaturedFromList passes content details to toggleWatchlist`() = runTest {
        val featured = fakeGenericContent(id = 1, name = "Test Movie", mediaType = MediaType.MOVIE)
        coEvery { homeInteractor.getTrendingMulti() } returns fakeHomeState(featured)
        every {
            listInteractor.verifyContentInLists(1, MediaType.MOVIE)
        } returns flowOf(mapOf(1 to false))
        coEvery {
            listInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any())
        } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.onEvent(HomeEvent.ToggleFeaturedFromList(listId = 1))
        awaitIO()

        coVerify {
            listInteractor.toggleWatchlist(
                currentStatus = false,
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = 1,
                title = "Test Movie",
                posterPath = "/poster.jpg",
                voteAverage = 7.5f
            )
        }
    }

    @Test
    fun `toggleFeaturedFromList passes cached fields when removing from list`() = runTest {
        val featured = fakeGenericContent(id = 1, name = "Test Movie", mediaType = MediaType.MOVIE)
        coEvery { homeInteractor.getTrendingMulti() } returns fakeHomeState(featured)
        every {
            listInteractor.verifyContentInLists(1, MediaType.MOVIE)
        } returns flowOf(mapOf(1 to true))
        coEvery {
            listInteractor.toggleWatchlist(any(), any(), any(), any(), any(), any(), any())
        } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()
        awaitIO()

        viewModel.onEvent(HomeEvent.ToggleFeaturedFromList(listId = 1))
        awaitIO()

        coVerify {
            listInteractor.toggleWatchlist(
                currentStatus = true,
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = 1,
                title = "Test Movie",
                posterPath = "/poster.jpg",
                voteAverage = 7.5f
            )
        }
    }
}
