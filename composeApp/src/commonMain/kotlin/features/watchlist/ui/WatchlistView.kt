package features.watchlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.empty_list_header
import cinetracker_kmp.composeapp.generated.resources.empty_list_message
import cinetracker_kmp.composeapp.generated.resources.empty_movie_list_message
import cinetracker_kmp.composeapp.generated.resources.empty_show_list_message
import common.domain.models.content.GenericContent
import common.domain.models.util.DataLoadStatus
import common.domain.models.util.MediaType
import common.domain.util.Constants.UNSELECTED_OPTION_INDEX
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.domain.util.UiConstants.SMALL_MARGIN
import common.ui.MainViewModel
import common.ui.components.popup.ClassicSnackbar
import common.ui.components.tab.GenericTabRow
import features.watchlist.WatchlistScreen
import features.watchlist.events.WatchlistEvent
import features.watchlist.ui.components.DeleteListDialog
import features.watchlist.ui.components.ListRemovePopUpMenu
import features.watchlist.ui.components.WatchlistBodyPlaceholder
import features.watchlist.ui.components.WatchlistCard
import features.watchlist.ui.components.WatchlistTabItem
import features.watchlist.ui.state.WatchlistSnackbarState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Watchlist(
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    Watchlist(
        viewModel = koinViewModel(),
        mainViewModel = koinViewModel(),
        goToDetails = goToDetails,
        goToErrorScreen = goToErrorScreen,
    )
}

@Composable
private fun Watchlist(
    viewModel: WatchlistViewModel,
    mainViewModel: MainViewModel,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    val loadState by viewModel.loadState.collectAsState()
    val allLists by viewModel.allLists.collectAsState()
    val listContent by viewModel.listContent.collectAsState()
    val selectedList by viewModel.selectedList
    val sortType by mainViewModel.watchlistSort.collectAsState()
    val snackbarState by viewModel.snackbarState
    val snackbarHostState = remember { SnackbarHostState() }

    if (allLists.isNotEmpty()) {
        AllListsLoadedState(
            allLists,
            viewModel,
            sortType,
            mainViewModel,
            snackbarState,
            snackbarHostState,
            loadState,
            listContent,
            selectedList,
            goToDetails,
            goToErrorScreen,
        )
    }
}

@Composable
private fun AllListsLoadedState(
    tabList: List<WatchlistTabItem>,
    viewModel: WatchlistViewModel,
    sortType: MediaType?,
    mainViewModel: MainViewModel,
    snackbarState: WatchlistSnackbarState,
    snackbarHostState: SnackbarHostState,
    loadState: DataLoadStatus,
    listContent: Map<Int, List<GenericContent>>,
    selectedList: Int,
    goToDetails: (Int, MediaType) -> Unit,
    goToErrorScreen: () -> Unit,
) {
    val refreshLists by mainViewModel.refreshLists.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    var showDeletePopUpMenu by remember { mutableStateOf(false) }
    var deletePopUpMenuOffset by remember { mutableStateOf(Offset.Zero) }
    val listToRemoveIndex = remember { mutableIntStateOf(UNSELECTED_OPTION_INDEX) }
    var displayDeleteDialog by remember { mutableStateOf(false) }

    val updateSelectedTab: (Int) -> Unit = { index ->
        if (tabList[index].listId == WatchlistTabItem.AddNewTab.listId) {
            mainViewModel.updateDisplayCreateNewList(true)
        } else {
            viewModel.onEvent(
                WatchlistEvent.SelectList(tabList[index]),
            )
        }
    }

    val removeItem: (Int, MediaType) -> Unit = { contentId, mediaType ->
        viewModel.onEvent(
            WatchlistEvent.RemoveItem(contentId, mediaType),
        )
    }

    val moveItemToList: (Int, MediaType, Int) -> Unit = { contentId, mediaType, listId ->
        viewModel.onEvent(
            WatchlistEvent.UpdateItemListId(contentId, mediaType, listId),
        )
    }

    LaunchedEffect(sortType) {
        viewModel.onEvent(
            WatchlistEvent.UpdateSortType(sortType),
        )
    }

    LaunchedEffect(Unit) {
        mainViewModel.updateCurrentScreen(WatchlistScreen.route())

        viewModel.onEvent(
            WatchlistEvent.LoadWatchlistData,
        )
    }

    LaunchedEffect(refreshLists) {
        if (refreshLists) {
            mainViewModel.setRefreshLists(false)
            viewModel.onEvent(WatchlistEvent.LoadAllLists)
        }
    }

//    SnackbarLaunchedEffect(snackbarState, snackbarHostState, viewModel, tabList)

    ClassicSnackbar(
        snackbarHostState = snackbarHostState,
        onActionClick = {
            viewModel.onEvent(WatchlistEvent.UndoItemAction)
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GenericTabRow(
                selectedTabIndex = selectedTabIndex,
                tabList = tabList,
                updateSelectedTab = { index, _ ->
                    updateSelectedTab(index)
                },
                onLongClick = { index, offset ->
                    if (index != WatchlistTabItem.WatchlistTab.tabIndex &&
                        index != WatchlistTabItem.WatchedTab.tabIndex
                    ) {
                        listToRemoveIndex.intValue = index
                        deletePopUpMenuOffset = offset
                        showDeletePopUpMenu = true
                    }
                },
            )
            when (loadState) {
                DataLoadStatus.Empty -> {
                    // Display empty screen
                }

                DataLoadStatus.Loading -> {
                    WatchlistBodyPlaceholder()
                }

                DataLoadStatus.Success -> {
                    val contentList = listContent[tabList[selectedTabIndex].listId]

                    WatchlistBody(
                        contentList = contentList.orEmpty(),
                        sortType = sortType,
                        selectedList = selectedList,
                        allLists = tabList,
                        goToDetails = goToDetails,
                        removeItem = removeItem,
                        moveItemToList = moveItemToList,
                    )
                }

                DataLoadStatus.Failed -> {
                    goToErrorScreen()
                }
            }
        }
    }

    ListRemovePopUpMenu(
        showRemoveMenu = showDeletePopUpMenu,
        menuOffset = deletePopUpMenuOffset,
        onRemoveList = {
            displayDeleteDialog = true
        },
        onDismiss = {
            showDeletePopUpMenu = false
        },
    )

    DeleteListDialog(
        displayDeleteDialog = displayDeleteDialog,
        listToRemoveIndex = listToRemoveIndex,
        viewModel = viewModel,
        tabList = tabList,
        onDialogDismiss = {
            displayDeleteDialog = false
        },
    )
}

// @Composable
// private fun SnackbarLaunchedEffect(
//    snackbarState: WatchlistSnackbarState,
//    snackbarHostState: SnackbarHostState,
//    viewModel: WatchlistViewModel,
//    tabList: List<WatchlistTabItem>,
// ) {
//    val context = LocalContext.current
//    LaunchedEffect(snackbarState) {
//        if (snackbarState.displaySnackbar.value) {
//            val watchlistTabItem = tabList.find { it.listId == snackbarState.listId }
//            val tabName = if (watchlistTabItem?.tabName.isNullOrEmpty()) {
//                context.resources.getString(
//                    getListLocalizedName(DefaultLists.getListById(snackbarState.listId ?: 0)),
//                )
//            } else {
//                watchlistTabItem?.tabName
//            }
//
//            tabName?.let {
//                val message = if (snackbarState.itemAction == WatchlistItemAction.ITEM_REMOVED) {
//                    context.resources.getString(
//                        R.string.snackbar_item_removed_from_list,
//                        it.capitalized(),
//                    )
//                } else {
//                    context.resources.getString(
//                        R.string.snackbar_item_moved_to_list,
//                        it.capitalized(),
//                    )
//                }
//                snackbarHostState.showSnackbar(message)
//                viewModel.onEvent(WatchlistEvent.OnSnackbarDismiss)
//            }
//        }
//    }
// }

@Composable
private fun WatchlistBody(
    contentList: List<GenericContent>,
    sortType: MediaType?,
    selectedList: Int,
    allLists: List<WatchlistTabItem>,
    goToDetails: (Int, MediaType) -> Unit,
    removeItem: (Int, MediaType) -> Unit,
    moveItemToList: (Int, MediaType, Int) -> Unit,
) {
    if (contentList.isNotEmpty()) {
        WatchlistContentLazyList(
            sortType = sortType,
            contentList = contentList,
            selectedList = selectedList,
            allLists = allLists,
            goToDetails = goToDetails,
            removeItem = removeItem,
            moveItemToList = moveItemToList,
        )
    } else {
        EmptyListMessage()
    }
}

@Composable
private fun WatchlistContentLazyList(
    sortType: MediaType?,
    contentList: List<GenericContent>,
    selectedList: Int,
    allLists: List<WatchlistTabItem>,
    goToDetails: (Int, MediaType) -> Unit,
    removeItem: (Int, MediaType) -> Unit,
    moveItemToList: (Int, MediaType, Int) -> Unit,
) {
    val sortedItems = if (sortType != null) {
        contentList.filter { it.mediaType == sortType }
    } else {
        contentList
    }

    if (sortedItems.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(all = SMALL_MARGIN.dp),
        ) {
            items(sortedItems) { mediaInfo ->
                WatchlistCard(
                    title = mediaInfo.name,
                    rating = mediaInfo.rating,
                    posterUrl = mediaInfo.posterPath,
                    mediaType = mediaInfo.mediaType,
                    selectedList = selectedList,
                    allLists = allLists,
                    onCardClick = {
                        goToDetails(mediaInfo.id, mediaInfo.mediaType)
                    },
                    onRemoveClick = {
                        removeItem(mediaInfo.id, mediaInfo.mediaType)
                    },
                    onMoveItemToList = { listId ->
                        moveItemToList(mediaInfo.id, mediaInfo.mediaType, listId)
                    },
                )
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
            }
        }
    } else {
        EmptyListMessage(mediaType = sortType)
    }
}

@Composable
private fun EmptyListMessage(
    mediaType: MediaType? = null,
) {
    val messageText = when (mediaType) {
        MediaType.MOVIE -> stringResource(resource = Res.string.empty_movie_list_message)
        MediaType.SHOW -> stringResource(resource = Res.string.empty_show_list_message)
        else -> stringResource(resource = Res.string.empty_list_message)
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        Text(
            text = stringResource(resource = Res.string.empty_list_header),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = messageText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(0.7f))
    }
}
