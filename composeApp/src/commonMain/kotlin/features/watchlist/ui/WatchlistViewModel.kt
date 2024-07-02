package features.watchlist.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.domain.models.content.GenericContent
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import features.watchlist.domain.WatchlistInteractor
import features.watchlist.events.WatchlistEvent
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.model.DefaultLists
import features.watchlist.ui.model.WatchlistItemAction
import features.watchlist.ui.state.WatchlistSnackbarState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchlistViewModel(
    private val watchlistInteractor: WatchlistInteractor,
) : ViewModel() {

    private val _loadState: MutableStateFlow<DataLoadStatus> = MutableStateFlow(
        DataLoadStatus.Empty,
    )
    val loadState: StateFlow<DataLoadStatus> get() = _loadState

    private val _allLists = MutableStateFlow(listOf<WatchlistTabItem>())
    val allLists: StateFlow<List<WatchlistTabItem>> get() = _allLists

    private val _listContent = MutableStateFlow<Map<Int, List<GenericContent>>>(emptyMap())
    val listContent: StateFlow<Map<Int, List<GenericContent>>> get() = _listContent

    val selectedList = mutableStateOf(DefaultLists.WATCHLIST.listId)

    private val sortType: MutableState<MediaType?> = mutableStateOf(null)

    private val _snackbarState: MutableState<WatchlistSnackbarState> = mutableStateOf(
        WatchlistSnackbarState(),
    )
    val snackbarState: MutableState<WatchlistSnackbarState> get() = _snackbarState
    private var lastItemAction: WatchlistItemAction? = null

    // Tabs
    private var _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> get() = _selectedTabIndex

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadAllLists(
                focusFirst = true,
            )
        }
    }

    fun onEvent(
        event: WatchlistEvent,
    ) {
        when (event) {
            is WatchlistEvent.LoadWatchlistData -> loadWatchlistData(showLoadingState = true)
            is WatchlistEvent.RemoveItem -> removeListItem(event.contentId, event.mediaType)
            is WatchlistEvent.SelectList -> updateSelectedTab(event.tabItem)
            is WatchlistEvent.UpdateSortType -> sortType.value = event.mediaType
            is WatchlistEvent.UpdateItemListId -> {
                updateItemListId(event.contentId, event.mediaType, event.listId)
            }
            is WatchlistEvent.OnSnackbarDismiss -> snackbarDismiss()
            is WatchlistEvent.UndoItemAction -> undoItemRemoved()
            is WatchlistEvent.LoadAllLists -> {
                viewModelScope.launch(Dispatchers.IO) {
                    loadAllLists(
                        focusLast = true,
                    )
                }
            }
            is WatchlistEvent.DeleteList -> {
                onDeleteList(event.listId)
            }
        }
    }

    private fun loadWatchlistData(
        showLoadingState: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val allListsData = mutableMapOf<Int, List<GenericContent>>()

            allLists.value.forEach { listItem ->
                val databaseItems = watchlistInteractor.getAllItems(listItem.listId)

                if (showLoadingState && databaseItems.isNotEmpty()) {
                    _loadState.value = DataLoadStatus.Loading
                }

                val listState = watchlistInteractor.fetchListDetails(databaseItems)
                if (listState.isFailed()) {
                    _loadState.value = DataLoadStatus.Failed
                    return@launch
                }

                allListsData[listItem.listId] = listState.listItems.value
            }

            _listContent.value = allListsData
            _loadState.value = DataLoadStatus.Success
        }
    }

    private fun removeListItem(
        contentId: Int,
        mediaType: MediaType,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.removeContentFromDatabase(
                contentId = contentId,
                mediaType = mediaType,
                listId = selectedList.value,
            )
            removeContentFromUiList(contentId, mediaType)

            triggerSnackbar(
                listId = selectedList.value,
                itemAction = WatchlistItemAction.ITEM_REMOVED,
            )
        }
    }

    private fun updateItemListId(
        contentId: Int,
        mediaType: MediaType,
        listId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.moveItemToList(
                contentId = contentId,
                mediaType = mediaType,
                currentListId = selectedList.value,
                newListId = listId,
            )
            loadWatchlistData(showLoadingState = false)

            triggerSnackbar(
                listId = listId,
                itemAction = WatchlistItemAction.ITEM_MOVED,
            )
        }
    }

    private fun triggerSnackbar(
        listId: Int,
        itemAction: WatchlistItemAction,
    ) {
        _snackbarState.value = WatchlistSnackbarState(
            listId = listId,
            itemAction = itemAction,
        ).apply {
            setSnackbarVisible()
        }
        lastItemAction = itemAction
    }

    private fun removeContentFromUiList(
        contentId: Int,
        mediaType: MediaType,
    ) {
        val listId = selectedList.value

        _listContent.value = _listContent.value.toMutableMap().apply {
            this[listId] = this[listId]?.filterNot {
                it.id == contentId && it.mediaType == mediaType
            } ?: emptyList()
        }
    }

    private fun undoItemRemoved() {
        viewModelScope.launch(Dispatchers.IO) {
            lastItemAction?.let { action ->
                if (action == WatchlistItemAction.ITEM_REMOVED) {
                    watchlistInteractor.undoItemRemoved()
                } else {
                    watchlistInteractor.undoMovedItem()
                }
                loadWatchlistData(showLoadingState = false)
            }
        }
    }

    private fun snackbarDismiss() {
        _snackbarState.value = WatchlistSnackbarState()
    }

    private suspend fun loadAllLists(
        focusLast: Boolean = false,
        focusFirst: Boolean = false,
    ) {
        _allLists.value = watchlistInteractor.getAllLists()
        withContext(Dispatchers.Main) {
            if (focusFirst) {
                updateSelectedTab(_allLists.value.firstOrNull())
            } else if (focusLast) {
                val isLastItemAddNew = _allLists.value.lastOrNull()?.listId ==
                    WatchlistTabItem.AddNewTab.listId
                val lastValidItem = if (isLastItemAddNew) {
                    _allLists.value[_allLists.value.lastIndex - 1]
                } else {
                    _allLists.value.lastOrNull()
                }
                updateSelectedTab(lastValidItem)
            }
        }
    }

    private fun updateSelectedTab(
        tabItem: WatchlistTabItem?,
    ) {
        if (tabItem != null) {
            selectedList.value = tabItem.listId
            _selectedTabIndex.value = tabItem.tabIndex
        }
    }

    private fun onDeleteList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            watchlistInteractor.deleteList(listId)
            loadAllLists(
                focusFirst = true,
            )
        }
    }
}
