package features.home.ui

import common.domain.models.util.DataLoadStatus
import features.home.domain.HomeInteractor
import features.home.events.HomeEvent
import features.home.ui.state.HomeState
import features.home.util.fakeHomeState
import features.watchlist.util.fakeGenericContent
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Default stubs so init block never throws
        coEvery { homeInteractor.getTrendingMulti() } returns fakeHomeState(fakeGenericContent())
        coEvery { homeInteractor.getTrendingPerson() } returns emptyList()
        coEvery { homeInteractor.getMoviesComingSoon() } returns emptyList()
        coEvery { homeInteractor.getAllWatchlist() } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = HomeViewModel(homeInteractor)

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
                mediaType = common.domain.models.util.MediaType.PERSON,
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
        coEvery { homeInteractor.getAllWatchlist() } returns listOf(fakeGenericContent(id = 5))

        val viewModel = createViewModel()
        advanceUntilIdle() // runs loadHomeScreen, which launches loadWatchlist on IO
        awaitIO() // waits for loadWatchlist IO coroutine to complete

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

    // ── ReloadWatchlist event ─────────────────────────────────────────────────

    @Test
    fun `ReloadWatchlist updates myWatchlist`() = runTest {
        coEvery { homeInteractor.getAllWatchlist() } returns listOf(fakeGenericContent(id = 7))

        val viewModel = createViewModel()
        advanceUntilIdle() // runs loadHomeScreen, which launches loadWatchlist on IO
        awaitIO() // waits for first loadWatchlist call

        viewModel.onEvent(HomeEvent.ReloadWatchlist)
        awaitIO() // waits for second loadWatchlist call

        assertEquals(1, viewModel.myWatchlist.value.size)
        assertEquals(7, viewModel.myWatchlist.value[0].id)
        coVerify(atLeast = 2) { homeInteractor.getAllWatchlist() }
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
                mediaType = common.domain.models.util.MediaType.PERSON,
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
}
