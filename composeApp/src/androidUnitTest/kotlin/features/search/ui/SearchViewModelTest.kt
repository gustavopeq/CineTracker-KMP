package features.search.ui

import androidx.paging.PagingData
import common.domain.models.util.MediaType
import common.util.UiConstants.SEARCH_DEBOUNCE_TIME_MS
import features.search.domain.SearchInteractor
import features.search.events.SearchEvent
import features.search.ui.components.SearchTypeFilterItem
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val interactor: SearchInteractor = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        // Stub for null mediaType (TopResults filter)
        every { interactor.onSearchQuery(any(), null) } returns flowOf(PagingData.empty())
        // Stub for non-null mediaType (Movies/Shows/Person filters)
        every { interactor.onSearchQuery(any(), any()) } returns flowOf(PagingData.empty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = SearchViewModel(interactor)

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `searchQuery starts empty`() {
        val viewModel = createViewModel()
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `searchFilterSelected starts as TopResults`() {
        val viewModel = createViewModel()
        assertIs<SearchTypeFilterItem.TopResults>(viewModel.searchFilterSelected.value)
    }

    // ── ClearSearchBar ────────────────────────────────────────────────────────

    @Test
    fun `ClearSearchBar clears searchQuery to empty string`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        advanceUntilIdle()

        viewModel.onEvent(SearchEvent.ClearSearchBar)
        advanceUntilIdle()

        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `ClearSearchBar cancels pending debounce job`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        // job is now pending (debounce not yet elapsed)

        viewModel.onEvent(SearchEvent.ClearSearchBar)
        advanceUntilIdle()

        // onClearSearchBar cancels the job but does not null it; verify it is no longer active
        assertTrue(viewModel.searchDebounceJob?.isActive != true)
    }

    // ── SearchQuery ───────────────────────────────────────────────────────────

    @Test
    fun `SearchQuery with empty string clears query`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        advanceUntilIdle()

        viewModel.onEvent(SearchEvent.SearchQuery(""))
        advanceUntilIdle()

        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `SearchQuery sets searchQuery value`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Inception"))

        assertEquals("Inception", viewModel.searchQuery.value)
    }

    @Test
    fun `SearchQuery does not call interactor before debounce delay`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        advanceTimeBy(SEARCH_DEBOUNCE_TIME_MS - 1)

        verify(exactly = 0) { interactor.onSearchQuery(any(), null) }
        verify(exactly = 0) { interactor.onSearchQuery(any(), any()) }
    }

    @Test
    fun `SearchQuery calls interactor after debounce delay`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        advanceTimeBy(SEARCH_DEBOUNCE_TIME_MS + 1)
        advanceUntilIdle()

        verify(atLeast = 1) { interactor.onSearchQuery(eq("Batman"), null) }
    }

    // ── Debounce cancellation ─────────────────────────────────────────────────

    @Test
    fun `Second SearchQuery cancels first debounce job`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        advanceTimeBy(SEARCH_DEBOUNCE_TIME_MS - 1)

        viewModel.onEvent(SearchEvent.SearchQuery("Superman"))
        advanceTimeBy(SEARCH_DEBOUNCE_TIME_MS + 1)
        advanceUntilIdle()

        verify(exactly = 0) { interactor.onSearchQuery(eq("Batman"), null) }
        verify(atLeast = 1) { interactor.onSearchQuery(eq("Superman"), null) }
    }

    // ── FilterTypeSelected ────────────────────────────────────────────────────

    @Test
    fun `FilterTypeSelected updates searchFilterSelected`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.FilterTypeSelected(SearchTypeFilterItem.Movies))
        advanceUntilIdle()

        assertIs<SearchTypeFilterItem.Movies>(viewModel.searchFilterSelected.value)
    }

    @Test
    fun `FilterTypeSelected triggers interactor with filter mediaType`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.SearchQuery("Batman"))
        advanceTimeBy(SEARCH_DEBOUNCE_TIME_MS + 1)
        advanceUntilIdle()

        viewModel.onEvent(SearchEvent.FilterTypeSelected(SearchTypeFilterItem.Movies))
        advanceUntilIdle()

        verify(atLeast = 1) { interactor.onSearchQuery(any(), eq(MediaType.MOVIE)) }
    }

    // ── OnError ───────────────────────────────────────────────────────────────

    @Test
    fun `OnError does not call interactor`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(SearchEvent.OnError)
        advanceUntilIdle()

        verify(exactly = 0) { interactor.onSearchQuery(any(), null) }
        verify(exactly = 0) { interactor.onSearchQuery(any(), any()) }
    }
}
