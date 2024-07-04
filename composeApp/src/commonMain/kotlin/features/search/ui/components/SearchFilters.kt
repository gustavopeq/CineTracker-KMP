package features.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import common.domain.models.util.MediaType
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.LARGE_PADDING
import common.util.UiConstants.SEARCH_FILTER_BUTTON_HEIGHT
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchFiltersRow(
    searchTypeSelected: SearchTypeFilterItem,
    onFilterTypeSelected: (SearchTypeFilterItem) -> Unit,
) {
    val searchFilters = listOf(
        SearchTypeFilterItem.TopResults,
        SearchTypeFilterItem.Movies,
        SearchTypeFilterItem.Shows,
        SearchTypeFilterItem.Person,
    )
    var selectedTabIndex by remember {
        mutableIntStateOf(searchFilters.indexOf(searchTypeSelected))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DEFAULT_PADDING.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            indicator = @Composable { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.Transparent,
                )
            },
            edgePadding = DEFAULT_MARGIN.dp,
            divider = { },
        ) {
            searchFilters.forEachIndexed { index, filterItem ->
                SearchTypeButton(
                    isSelected = index == selectedTabIndex,
                    searchTypeItem = filterItem,
                    onFilterTypeSelected = {
                        onFilterTypeSelected(filterItem)
                        selectedTabIndex = index
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchTypeButton(
    isSelected: Boolean,
    searchTypeItem: SearchTypeFilterItem,
    onFilterTypeSelected: (MediaType?) -> Unit,
) {
    val btnColor = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else SecondaryGreyColor

    Surface(
        color = btnColor,
        shape = ButtonDefaults.outlinedShape,
        modifier = Modifier
            .height(SEARCH_FILTER_BUTTON_HEIGHT.dp)
            .clickable(
                onClick = {
                    onFilterTypeSelected(searchTypeItem.mediaType)
                },
            ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = LARGE_PADDING.dp),
                text = stringResource(resource = searchTypeItem.tabResId),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = textColor,
            )
        }
    }
}
