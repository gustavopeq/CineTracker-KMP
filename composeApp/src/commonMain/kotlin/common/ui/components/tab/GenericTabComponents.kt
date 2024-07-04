package common.ui.components.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.add_new_tab_description
import cinetracker_kmp.composeapp.generated.resources.ic_watchlist_add_list
import common.ui.theme.MainBarGreyColor
import common.util.UiConstants.BACKGROUND_INDEX
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.GENERIC_TAB_MAX_WIDTH
import common.util.UiConstants.LARGE_PADDING
import common.util.UiConstants.WATCHLIST_ADD_NEW_ICON_SIZE
import common.util.removeParentPadding
import features.watchlist.ui.components.WatchlistTabItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun setupGenericTabs(
    tabList: List<TabItem>,
    onTabSelected: (Int) -> Unit = {},
): Triple<List<TabItem>, State<Int>, (Int, Boolean) -> Unit> {
    tabList.forEachIndexed { index, tabItem ->
        tabItem.tabIndex = index
    }

    val selectedTabIndex = rememberSaveable {
        mutableIntStateOf(tabList.firstOrNull()?.tabIndex ?: 0)
    }

    val updateSelectedTab: (Int, Boolean) -> Unit = { index, focusSelectedTab ->
        if (focusSelectedTab) {
            selectedTabIndex.intValue = index
        }
        onTabSelected(index)
    }

    return Triple(tabList, selectedTabIndex, updateSelectedTab)
}

@Composable
fun GenericTabRow(
    selectedTabIndex: Int,
    tabList: List<TabItem>,
    updateSelectedTab: (Int, Boolean) -> Unit,
    onLongClick: (Int, Offset) -> Unit = { _, _ -> },
) {
    ScrollableTabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.secondary,
            )
        },
        divider = { },
        containerColor = Color.Transparent,
        edgePadding = 0.dp,
    ) {
        tabList.forEachIndexed { index, mediaTypeTabItem ->
            if (mediaTypeTabItem == WatchlistTabItem.AddNewTab) {
                AddNewTab(
                    tabIndex = index,
                    onClick = {
                        updateSelectedTab(index, false)
                    },
                )
            } else {
                val tabName = mediaTypeTabItem.tabResId?.let {
                    stringResource(resource = it)
                } ?: mediaTypeTabItem.tabName

                GenericTab(
                    text = tabName.orEmpty(),
                    isSelected = selectedTabIndex == index,
                    onClick = {
                        updateSelectedTab(index, true)
                    },
                    onLongClick = { offset ->
                        onLongClick(index, offset)
                    },
                )
            }
        }
    }
    Divider(
        color = MainBarGreyColor,
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-1).dp)
            .zIndex(BACKGROUND_INDEX)
            .removeParentPadding(DEFAULT_MARGIN.dp),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenericTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: (Offset) -> Unit,
) {
    var tabOffset by remember { mutableStateOf(Offset.Zero) }
    val tabModifier = if (isSelected) {
        Modifier.basicMarquee(
            iterations = 1,
            spacing = MarqueeSpacing(10.dp),
        )
    } else {
        Modifier
    }

    Tab(
        modifier = Modifier
            .padding(horizontal = DEFAULT_PADDING.dp)
            .wrapContentWidth()
            .onGloballyPositioned { coordinates ->
                tabOffset = coordinates.localToRoot(Offset.Zero)
            },
        selected = isSelected,
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier.combinedClickable(
                onLongClick = { onLongClick(tabOffset) },
                onClick = { onClick() },
            ).widthIn(max = GENERIC_TAB_MAX_WIDTH.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = tabModifier,
                text = text.uppercase(),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.tertiary
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        }
        Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
    }
}

@Composable
fun AddNewTab(
    tabIndex: Int,
    onClick: (Int) -> Unit,
) {
    Tab(
        modifier = Modifier.padding(horizontal = DEFAULT_PADDING.dp),
        selected = false,
        onClick = { onClick(tabIndex) },
    ) {
        Icon(
            modifier = Modifier.size(WATCHLIST_ADD_NEW_ICON_SIZE.dp),
            painter = painterResource(resource = Res.drawable.ic_watchlist_add_list),
            contentDescription = stringResource(resource = Res.string.add_new_tab_description),
            tint = MaterialTheme.colorScheme.secondary,
        )
        Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
    }
}
