package features.watchlist.ui

import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import common.util.fakeContentEntity
import features.watchlist.domain.WatchlistInteractor
import features.watchlist.events.WatchlistEvent
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.DefaultLists
import features.watchlist.ui.model.WatchlistItemAction
import features.watchlist.ui.state.WatchlistState
import features.watchlist.util.fakeGenericContent
import features.watchlist.util.successfulWatchlistState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Waits for Dispatchers.IO coroutines launched by the ViewModel to complete.
 * Necessary because viewModelScope.launch(Dispatchers.IO) runs on real threads
 * that are NOT controlled by StandardTestDispatcher.
 */
private fun awaitIO(ms: Long = 300L) = Thread.sleep(ms)

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {

    private val watchlistInteractor: WatchlistInteractor = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Default stubs so init block never throws
        coEvery { watchlistInteractor.getAllLists() } returns listOf(
            WatchlistTabItem.WatchlistTab,
            WatchlistTabItem.WatchedTab
        )
        coEvery { watchlistInteractor.getAllItems(any()) } returns emptyList()
        coEvery { watchlistInteractor.fetchListDetails(any()) } returns WatchlistState()
        coEvery { watchlistInteractor.removeContentFromDatabase(any(), any(), any()) } returns Unit
        coEvery { watchlistInteractor.moveItemToList(any(), any(), any(), any()) } returns Unit
        coEvery { watchlistInteractor.undoItemRemoved() } returns Unit
        coEvery { watchlistInteractor.undoMovedItem() } returns Unit
        coEvery { watchlistInteractor.deleteList(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel() = WatchlistViewModel(watchlistInteractor)

    // ── Init ──────────────────────────────────────────────────────────────────

    @Test
    fun `loadState starts as Empty before coroutines run`() {
        val viewModel = createViewModel()
        assertEquals(DataLoadStatus.Empty, viewModel.loadState.value)
        // Allow the init coroutine to complete before tearDown resets the Main dispatcher
        awaitIO()
    }

    @Test
    fun `allLists is populated and selectedList is set to first tab after init`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        assertTrue(viewModel.allLists.value.isNotEmpty())
        assertEquals(DefaultLists.WATCHLIST.listId, viewModel.selectedList.value)
    }

    // ── LoadWatchlistData — success ───────────────────────────────────────────

    @Test
    fun `loadState transitions to Success after LoadWatchlistData`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.LoadWatchlistData)
        awaitIO()

        assertEquals(DataLoadStatus.Success, viewModel.loadState.value)
    }

    @Test
    fun `LoadWatchlistData populates listContent map keyed by listId`() = runTest {
        val content = fakeGenericContent(id = 1)
        coEvery { watchlistInteractor.getAllItems(WatchlistTabItem.WatchlistTab.listId) } returns listOf(
            fakeContentEntity(1, WatchlistTabItem.WatchlistTab.listId)
        )
        coEvery { watchlistInteractor.fetchListDetails(any()) } returns successfulWatchlistState(content)

        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.LoadWatchlistData)
        awaitIO()

        assertTrue(viewModel.listContent.value.isNotEmpty())
        assertEquals(1, viewModel.listContent.value[WatchlistTabItem.WatchlistTab.listId]?.size)
    }

    // ── LoadWatchlistData — failure ───────────────────────────────────────────

    @Test
    fun `loadState is Failed when fetchListDetails returns error state`() = runTest {
        val errorState = WatchlistState().apply { setError("500") }
        coEvery { watchlistInteractor.getAllItems(any()) } returns listOf(fakeContentEntity(1, 1))
        coEvery { watchlistInteractor.fetchListDetails(any()) } returns errorState

        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.LoadWatchlistData)
        awaitIO()

        assertEquals(DataLoadStatus.Failed, viewModel.loadState.value)
    }

    // ── RemoveItem ────────────────────────────────────────────────────────────

    @Test
    fun `RemoveItem calls removeContentFromDatabase with correct args`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.RemoveItem(contentId = 1, mediaType = MediaType.MOVIE))
        awaitIO()

        coVerify {
            watchlistInteractor.removeContentFromDatabase(
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = DefaultLists.WATCHLIST.listId
            )
        }
    }

    @Test
    fun `RemoveItem removes item from listContent map`() = runTest {
        val content = fakeGenericContent(id = 1, mediaType = MediaType.MOVIE)
        coEvery { watchlistInteractor.getAllItems(WatchlistTabItem.WatchlistTab.listId) } returns listOf(
            fakeContentEntity(1, WatchlistTabItem.WatchlistTab.listId)
        )
        coEvery { watchlistInteractor.fetchListDetails(any()) } returns successfulWatchlistState(content)

        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.LoadWatchlistData)
        awaitIO()
        assertEquals(1, viewModel.listContent.value[WatchlistTabItem.WatchlistTab.listId]?.size)

        viewModel.onEvent(WatchlistEvent.RemoveItem(contentId = 1, mediaType = MediaType.MOVIE))
        awaitIO()

        assertTrue(viewModel.listContent.value[WatchlistTabItem.WatchlistTab.listId].isNullOrEmpty())
    }

    @Test
    fun `RemoveItem sets snackbar with ITEM_REMOVED action and makes it visible`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.RemoveItem(contentId = 1, mediaType = MediaType.MOVIE))
        awaitIO()

        assertEquals(WatchlistItemAction.ITEM_REMOVED, viewModel.snackbarState.value.itemAction)
        assertTrue(viewModel.snackbarState.value.displaySnackbar.value)
    }

    // ── UpdateItemListId ──────────────────────────────────────────────────────

    @Test
    fun `UpdateItemListId calls moveItemToList with correct args`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(
            WatchlistEvent.UpdateItemListId(
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = DefaultLists.WATCHED.listId
            )
        )
        awaitIO()

        coVerify {
            watchlistInteractor.moveItemToList(
                contentId = 1,
                mediaType = MediaType.MOVIE,
                currentListId = DefaultLists.WATCHLIST.listId,
                newListId = DefaultLists.WATCHED.listId
            )
        }
    }

    @Test
    fun `UpdateItemListId sets snackbar with ITEM_MOVED action and makes it visible`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(
            WatchlistEvent.UpdateItemListId(
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = DefaultLists.WATCHED.listId
            )
        )
        awaitIO()

        assertEquals(WatchlistItemAction.ITEM_MOVED, viewModel.snackbarState.value.itemAction)
        assertTrue(viewModel.snackbarState.value.displaySnackbar.value)
    }

    // ── SelectList ────────────────────────────────────────────────────────────

    @Test
    fun `SelectList updates selectedList value`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.SelectList(WatchlistTabItem.WatchedTab))

        assertEquals(WatchlistTabItem.WatchedTab.listId, viewModel.selectedList.value)
    }

    // ── UndoItemAction ────────────────────────────────────────────────────────

    @Test
    fun `UndoItemAction calls undoItemRemoved after RemoveItem`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.RemoveItem(contentId = 1, mediaType = MediaType.MOVIE))
        awaitIO()

        viewModel.onEvent(WatchlistEvent.UndoItemAction)
        awaitIO()

        coVerify { watchlistInteractor.undoItemRemoved() }
        coVerify(exactly = 0) { watchlistInteractor.undoMovedItem() }
    }

    @Test
    fun `UndoItemAction calls undoMovedItem after UpdateItemListId`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(
            WatchlistEvent.UpdateItemListId(
                contentId = 1,
                mediaType = MediaType.MOVIE,
                listId = DefaultLists.WATCHED.listId
            )
        )
        awaitIO()

        viewModel.onEvent(WatchlistEvent.UndoItemAction)
        awaitIO()

        coVerify { watchlistInteractor.undoMovedItem() }
        coVerify(exactly = 0) { watchlistInteractor.undoItemRemoved() }
    }

    // ── OnSnackbarDismiss ─────────────────────────────────────────────────────

    @Test
    fun `OnSnackbarDismiss resets snackbar to not visible`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.RemoveItem(contentId = 1, mediaType = MediaType.MOVIE))
        awaitIO()
        assertTrue(viewModel.snackbarState.value.displaySnackbar.value)

        viewModel.onEvent(WatchlistEvent.OnSnackbarDismiss)

        assertFalse(viewModel.snackbarState.value.displaySnackbar.value)
    }

    // ── LoadAllLists ──────────────────────────────────────────────────────────

    @Test
    fun `LoadAllLists calls getAllLists again`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.LoadAllLists)
        awaitIO()

        // Called once during init + once for LoadAllLists
        coVerify(atLeast = 2) { watchlistInteractor.getAllLists() }
    }

    // ── DeleteList ────────────────────────────────────────────────────────────

    @Test
    fun `DeleteList calls watchlistInteractor deleteList with correct listId`() = runTest {
        val viewModel = createViewModel()
        awaitIO()

        viewModel.onEvent(WatchlistEvent.DeleteList(5))
        awaitIO()

        coVerify { watchlistInteractor.deleteList(5) }
    }
}
