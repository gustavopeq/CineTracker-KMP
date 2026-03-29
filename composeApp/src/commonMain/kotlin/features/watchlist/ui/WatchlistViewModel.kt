package features.watchlist.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.domain.models.content.GenericContent
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import common.ui.WatchlistSort
import features.watchlist.domain.WatchlistInteractor
import features.watchlist.events.WatchlistEvent
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.WatchlistItemAction
import features.watchlist.ui.state.WatchlistSnackbarState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WatchlistViewModel(private val watchlistInteractor: WatchlistInteractor) : ViewModel() {

    private val _loadState: MutableStateFlow<DataLoadStatus> = MutableStateFlow(DataLoadStatus.Empty)
    val loadState: StateFlow<DataLoadStatus> get() = _loadState

    private val _allLists = MutableStateFlow(listOf<WatchlistTabItem>())
    val allLists: StateFlow<List<WatchlistTabItem>> get() = _allLists

    private val _listContent = MutableStateFlow<List<GenericContent>>(emptyList())
    val listContent: StateFlow<List<GenericContent>> get() = _listContent

    val selectedList = mutableStateOf(1)

    private val sortType: MutableState<WatchlistSort> = mutableStateOf(WatchlistSort())

    private val _snackbarState: MutableState<WatchlistSnackbarState> = mutableStateOf(WatchlistSnackbarState())
    val snackbarState: MutableState<WatchlistSnackbarState> get() = _snackbarState
    private var lastItemAction: WatchlistItemAction? = null

    private var _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> get() = _selectedTabIndex

    private var listContentJob: Job? = null

    init {
        collectAllLists()
    }

    fun onEvent(event: WatchlistEvent) {
        when (event) {
            is WatchlistEvent.RemoveItem -> removeListItem(event.contentId, event.mediaType)
            is WatchlistEvent.SelectList -> updateSelectedTab(event.tabItem)
            is WatchlistEvent.UpdateSortType -> sortType.value = event.watchlistSort
            is WatchlistEvent.UpdateItemListId -> {
                updateItemListId(event.contentId, event.mediaType, event.listId)
            }
            is WatchlistEvent.OnSnackbarDismiss -> snackbarDismiss()
            is WatchlistEvent.UndoItemAction -> undoItemRemoved()
            is WatchlistEvent.DeleteList -> onDeleteList(event.listId)
        }
    }

    private fun collectAllLists() {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.getAllLists().collectLatest { lists ->
                _allLists.value = lists
                if (lists.isNotEmpty() && _loadState.value == DataLoadStatus.Empty) {
                    val firstTab = lists.firstOrNull()
                    if (firstTab != null) {
                        updateSelectedTab(firstTab)
                    }
                }
            }
        }
    }

    private fun collectListContent(listId: Int) {
        listContentJob?.cancel()
        listContentJob = viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.getListContentWithRatings(listId).collectLatest { content ->
                _listContent.value = content
                if (_loadState.value != DataLoadStatus.Success) {
                    _loadState.value = DataLoadStatus.Success
                }
            }
        }
    }

    private fun removeListItem(contentId: Int, mediaType: MediaType) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.removeContentFromDatabase(
                contentId = contentId,
                mediaType = mediaType,
                listId = selectedList.value
            )
            triggerSnackbar(listId = selectedList.value, itemAction = WatchlistItemAction.ITEM_REMOVED)
        }
    }

    private fun updateItemListId(contentId: Int, mediaType: MediaType, listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.moveItemToList(
                contentId = contentId,
                mediaType = mediaType,
                currentListId = selectedList.value,
                newListId = listId
            )
            triggerSnackbar(listId = listId, itemAction = WatchlistItemAction.ITEM_MOVED)
        }
    }

    private fun triggerSnackbar(listId: Int, itemAction: WatchlistItemAction) {
        _snackbarState.value = WatchlistSnackbarState(
            listId = listId,
            itemAction = itemAction
        ).apply { setSnackbarVisible() }
        lastItemAction = itemAction
    }

    private fun undoItemRemoved() {
        viewModelScope.launch(Dispatchers.IO) {
            lastItemAction?.let { action ->
                if (action == WatchlistItemAction.ITEM_REMOVED) {
                    watchlistInteractor.undoItemRemoved()
                } else {
                    watchlistInteractor.undoMovedItem()
                }
            }
        }
    }

    private fun snackbarDismiss() {
        _snackbarState.value = WatchlistSnackbarState()
    }

    private fun updateSelectedTab(tabItem: WatchlistTabItem?) {
        if (tabItem != null) {
            selectedList.value = tabItem.listId
            _selectedTabIndex.value = tabItem.tabIndex
            collectListContent(tabItem.listId)
        }
    }

    private fun onDeleteList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.deleteList(listId)
        }
    }
}
