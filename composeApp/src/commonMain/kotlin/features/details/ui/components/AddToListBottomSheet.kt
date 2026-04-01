package features.details.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.add_to_list_no_thanks
import cinetracker_kmp.composeapp.generated.resources.add_to_list_sheet_title
import common.domain.models.list.ListItem
import common.ui.components.SystemNavBarSpacer
import common.ui.components.bottomsheet.GenericBottomSheet
import common.ui.theme.DividerGrey
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.LARGE_PADDING
import common.util.UiConstants.SMALL_MARGIN
import common.util.capitalized
import features.watchlist.ui.model.DefaultLists
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddToListBottomSheet(lists: List<ListItem>, onListSelected: (Int) -> Unit, onDismiss: () -> Unit) {
    GenericBottomSheet(
        dismissBottomSheet = { onDismiss() },
        headerText = stringResource(resource = Res.string.add_to_list_sheet_title)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(lists) { listItem ->
                val listName = when {
                    listItem.isDefault -> stringResource(
                        DefaultLists.getListLocalizedName(DefaultLists.getListById(listItem.id))
                    )
                    else -> listItem.name
                }
                ListSelectRow(
                    listName = listName,
                    onSelect = { onListSelected(listItem.id) }
                )
                HorizontalDivider(color = DividerGrey)
            }
            item {
                Spacer(modifier = Modifier.height(SMALL_MARGIN.dp))
                TextButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DEFAULT_MARGIN.dp)
                ) {
                    Text(
                        text = stringResource(resource = Res.string.add_to_list_no_thanks),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SystemNavBarSpacer()
            }
        }
    }
}

@Composable
private fun ListSelectRow(listName: String, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(horizontal = DEFAULT_MARGIN.dp, vertical = LARGE_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = listName.capitalized(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
