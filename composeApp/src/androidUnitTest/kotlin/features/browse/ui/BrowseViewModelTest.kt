package features.browse.ui

import androidx.paging.PagingData
import common.domain.models.util.ContentListType
import common.domain.models.util.MediaType
import common.domain.models.util.SortTypeItem
import features.browse.domain.BrowseInteractor
import features.browse.events.BrowseEvent
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
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

@OptIn(ExperimentalCoroutinesApi::class)
class BrowseViewModelTest {

    private val interactor: BrowseInteractor = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { interactor.getMediaContentListPager(any(), any()) } returns flowOf(PagingData.empty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = BrowseViewModel(interactor)

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `moviePager and showPager start as empty and interactor is not called at init`() = runTest {
        createViewModel()
        advanceUntilIdle()

        verify(exactly = 0) { interactor.getMediaContentListPager(any(), any()) }
    }

    // ── UpdateSortType — MOVIE ────────────────────────────────────────────────

    @Test
    fun `UpdateSortType for MOVIE calls interactor with correct arguments`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(BrowseEvent.UpdateSortType(SortTypeItem.Popular, MediaType.MOVIE))
        advanceUntilIdle()

        verify(exactly = 1) {
            interactor.getMediaContentListPager(ContentListType.MOVIE_POPULAR, MediaType.MOVIE)
        }
    }

    // ── UpdateSortType — SHOW ─────────────────────────────────────────────────

    @Test
    fun `UpdateSortType for SHOW calls interactor with correct arguments`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(BrowseEvent.UpdateSortType(SortTypeItem.ShowPopular, MediaType.SHOW))
        advanceUntilIdle()

        verify(exactly = 1) {
            interactor.getMediaContentListPager(ContentListType.SHOW_POPULAR, MediaType.SHOW)
        }
    }

    // ── Sort-type guard ───────────────────────────────────────────────────────

    @Test
    fun `UpdateSortType with same sort type does not call interactor again`() = runTest {
        val viewModel = createViewModel()
        val event = BrowseEvent.UpdateSortType(SortTypeItem.Popular, MediaType.MOVIE)

        viewModel.onEvent(event)
        advanceUntilIdle()
        viewModel.onEvent(event)
        advanceUntilIdle()

        verify(exactly = 1) { interactor.getMediaContentListPager(any(), any()) }
    }

    @Test
    fun `UpdateSortType with PERSON mediaType does not call interactor`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(BrowseEvent.UpdateSortType(SortTypeItem.Popular, MediaType.PERSON))
        advanceUntilIdle()

        verify(exactly = 0) { interactor.getMediaContentListPager(any(), any()) }
    }

    // ── OnError reset ─────────────────────────────────────────────────────────

    @Test
    fun `OnError resets sort type allowing same sort type to call interactor again`() = runTest {
        val viewModel = createViewModel()
        val event = BrowseEvent.UpdateSortType(SortTypeItem.Popular, MediaType.MOVIE)

        viewModel.onEvent(event)
        advanceUntilIdle()

        viewModel.onEvent(BrowseEvent.OnError)

        viewModel.onEvent(event)
        advanceUntilIdle()

        verify(exactly = 2) { interactor.getMediaContentListPager(any(), any()) }
    }
}
