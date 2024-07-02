package features.watchlist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.move_to_list_option_popup_menu
import cinetracker_kmp.composeapp.generated.resources.move_to_other_list_header
import cinetracker_kmp.composeapp.generated.resources.move_to_other_list_item
import cinetracker_kmp.composeapp.generated.resources.remove_option_popup_menu
import common.domain.util.Constants.DEFAULT_LISTS_SIZE
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.domain.util.UiConstants.LARGE_MARGIN
import common.domain.util.UiConstants.LARGE_PADDING
import common.domain.util.capitalized
import common.ui.components.bottomsheet.GenericBottomSheet
import common.ui.components.popup.GenericPopupMenu
import common.ui.components.popup.PopupMenuItem
import features.watchlist.ui.model.DefaultLists
import org.jetbrains.compose.resources.stringResource

@Composable
fun CardOptionsPopUpMenu(
    showMenu: Boolean,
    selectedListId: Int,
    allLists: List<WatchlistTabItem>,
    onDismissRequest: () -> Unit,
    onRemoveClick: () -> Unit,
    onMoveItemToList: (Int) -> Unit,
) {
    val selectedListTabItem = allLists.find { it.listId == selectedListId }
    val selectedListName = if (selectedListTabItem?.tabResId != null) {
        stringResource(resource = selectedListTabItem.tabResId!!)
    } else {
        selectedListTabItem?.tabName
    }

    val allListsFiltered = allLists.filterNot {
        it.listId == DefaultLists.ADD_NEW.listId || it.listId == selectedListId
    }
    var displayOtherListsPanel by remember { mutableStateOf(false) }
    val updateDisplayOtherListsPanel: (Boolean) -> Unit = {
        displayOtherListsPanel = it
    }
    val secondaryList = DefaultLists.getOtherList(selectedListId)
    val secondaryListName = DefaultLists.getListLocalizedName(
        DefaultLists.getListById(secondaryList.listId),
    )

    val menuItems = createMenuItems(
        selectedListName = selectedListName.orEmpty(),
        secondaryListName = stringResource(resource = secondaryListName),
        allLists = allListsFiltered,
        onRemoveClick = onRemoveClick,
        onMoveItemToSecondaryList = {
            onMoveItemToList(secondaryList.listId)
        },
        onShowOtherListsPanel = {
            updateDisplayOtherListsPanel(true)
        },
    )

    GenericPopupMenu(
        showMenu = showMenu,
        onDismissRequest = onDismissRequest,
        menuItems = menuItems,
    )

    if (displayOtherListsPanel) {
        OtherListsPanel(
            allLists = allListsFiltered,
            updateDisplayOtherListsPanel = updateDisplayOtherListsPanel,
            onDismissRequest = onDismissRequest,
            onMoveItemToList = onMoveItemToList,
        )
    }
}

@Composable
private fun createMenuItems(
    selectedListName: String,
    secondaryListName: String,
    allLists: List<WatchlistTabItem>,
    onRemoveClick: () -> Unit,
    onMoveItemToSecondaryList: () -> Unit,
    onShowOtherListsPanel: () -> Unit,
): List<PopupMenuItem> {
    val removeItem = PopupMenuItem(
        title = stringResource(
            resource = Res.string.remove_option_popup_menu,
            selectedListName.capitalized(),
        ),
        textColor = MaterialTheme.colorScheme.error,
        onClick = onRemoveClick,
    )

    val menuItems = if (allLists.size < DEFAULT_LISTS_SIZE) {
        listOf(
            removeItem,
            PopupMenuItem(
                title = stringResource(
                    resource = Res.string.move_to_list_option_popup_menu,
                    secondaryListName,
                ),
                onClick = onMoveItemToSecondaryList,
            ),
        )
    } else {
        listOf(
            removeItem,
            PopupMenuItem(
                title = stringResource(resource = Res.string.move_to_other_list_item),
                onClick = onShowOtherListsPanel,
            ),
        )
    }
    return menuItems
}

@Composable
private fun OtherListsPanel(
    allLists: List<WatchlistTabItem>,
    updateDisplayOtherListsPanel: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onMoveItemToList: (Int) -> Unit,
) {
    fun dismissBottomSheet() {
        updateDisplayOtherListsPanel(false)
        onDismissRequest()
    }

    GenericBottomSheet(
        dismissBottomSheet = {
            dismissBottomSheet()
        },
        headerText = stringResource(resource = Res.string.move_to_other_list_header),
    ) {
        LazyColumn {
            items(allLists) { list ->
                val listName = if (list.tabResId != null) {
                    stringResource(resource = list.tabResId!!)
                } else {
                    list.tabName
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onMoveItemToList(list.listId)
                            dismissBottomSheet()
                        },
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = DEFAULT_PADDING.dp),
                            text = listName?.capitalized().orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))
            }
        }
    }
}
