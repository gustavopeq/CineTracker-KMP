package features.details.ui.components.otherlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import common.domain.util.UiConstants.LARGE_MARGIN
import common.domain.util.capitalized
import common.ui.components.SystemNavBarSpacer
import common.ui.components.bottomsheet.GenericBottomSheet
import org.jetbrains.compose.resources.stringResource

@Composable
fun OtherListsBottomSheet(
    allLists: List<ListItem>,
    contentInListStatus: Map<Int, Boolean>,
    onToggleList: (Int) -> Unit,
    onClosePanel: () -> Unit,
) {
//    BackHandler {
//        onClosePanel()
//    }

    GenericBottomSheet(
        dismissBottomSheet = {
            onClosePanel()
        },
        headerText = stringResource(resource = Res.string.manage_other_lists_header),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(contentInListStatus.toList()) { mapItem ->
                val listName = allLists.find {
                    it.id == mapItem.first
                }?.name

                val isContentInList = mapItem.second

                if (listName != null) {
                    ListCheckboxRow(
                        isContentInList = isContentInList,
                        listName = listName,
                        onToggleList = { onToggleList(mapItem.first) },
                    )
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
private fun ListCheckboxRow(
    isContentInList: Boolean,
    listName: String,
    onToggleList: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            onToggleList()
        },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isContentInList,
            onCheckedChange = {
                onToggleList()
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
            ),
        )
        Text(
            text = listName.capitalized(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
