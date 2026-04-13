package common.ui.components.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.manage_other_lists_header
import common.domain.models.list.ListItem
import common.ui.components.AppSwitch
import common.ui.components.SystemNavBarSpacer
import common.ui.components.bottomsheet.GenericBottomSheet
import common.ui.theme.DividerGrey
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.LARGE_MARGIN
import common.util.UiConstants.LARGE_PADDING
import common.util.capitalized
import features.watchlist.ui.model.DefaultLists
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageListsBottomSheet(
    allLists: List<ListItem>,
    contentInListStatus: Map<Int, Boolean>,
    onToggleList: (Int) -> Unit,
    onClosePanel: () -> Unit,
    headerText: String = stringResource(resource = Res.string.manage_other_lists_header)
) {
    GenericBottomSheet(
        dismissBottomSheet = { onClosePanel() },
        headerText = headerText
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(contentInListStatus.toList()) { mapItem ->
                val listItem = allLists.find { it.id == mapItem.first }
                val listName = when {
                    listItem?.isDefault == true -> stringResource(
                        DefaultLists.getListLocalizedName(DefaultLists.getListById(listItem.id))
                    )
                    else -> listItem?.name
                }

                val isContentInList = mapItem.second

                if (listName != null) {
                    ListToggleRow(
                        isContentInList = isContentInList,
                        listName = listName,
                        onToggleList = { onToggleList(mapItem.first) }
                    )
                    HorizontalDivider(color = DividerGrey)
                }
            }
            item {
                Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))
                SystemNavBarSpacer()
            }
        }
    }
}

@Composable
private fun ListToggleRow(isContentInList: Boolean, listName: String, onToggleList: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleList() }
            .padding(horizontal = DEFAULT_MARGIN.dp, vertical = LARGE_PADDING.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = listName.capitalized(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        AppSwitch(
            checked = isContentInList,
            onCheckedChange = { onToggleList() }
        )
    }
}
