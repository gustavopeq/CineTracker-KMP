package features.details.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.back_arrow_description
import cinetracker_kmp.composeapp.generated.resources.ic_back_arrow
import common.domain.util.UiConstants
import common.domain.util.UiConstants.RETURN_TOP_BAR_HEIGHT
import common.domain.util.dpToPx
import common.ui.components.classicVerticalGradientBrush
import features.details.util.mapValueToRange
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DetailsTopBar(
    contentTitle: String,
    currentHeaderPosY: Float,
    initialHeaderPosY: Float? = null,
    showWatchlistButton: Boolean,
    contentInWatchlistStatus: Map<Int, Boolean>,
    onBackBtnPress: () -> Unit,
    toggleWatchlist: (Int) -> Unit,
    showOtherListsPanel: (Boolean) -> Unit,
) {
    val barHeightFloat = dpToPx(RETURN_TOP_BAR_HEIGHT.dp, density = LocalDensity.current)

    val alphaLevel = if (initialHeaderPosY != null) {
        currentHeaderPosY.mapValueToRange(
            initialHeaderPosY - barHeightFloat * 2,
        )
    } else {
        1f
    }

    val gradientColor = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primary.copy(
            alpha = (1f - (alphaLevel * 2)).coerceIn(minimumValue = 0f, maximumValue = 1f),
        ),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RETURN_TOP_BAR_HEIGHT.dp)
            .classicVerticalGradientBrush(
                colorList = gradientColor,
            )
            .zIndex(UiConstants.FOREGROUND_INDEX),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onBackBtnPress() },
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_back_arrow),
                contentDescription = stringResource(resource = Res.string.back_arrow_description),
            )
        }

        Box(
            modifier = Modifier.weight(1f),
        ) {
            this@Row.AnimatedVisibility(
                visible = currentHeaderPosY < 0,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = contentTitle,
                    style = MaterialTheme.typography.displaySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }

//        if (showWatchlistButton) {
//            WatchlistButtonIcon(
//                contentInWatchlistStatus = contentInWatchlistStatus,
//                toggleWatchlist = toggleWatchlist,
//                showOtherListsPanel = showOtherListsPanel,
//            )
//        }
    }
}

// @Composable
// private fun WatchlistButtonIcon(
//    contentInWatchlistStatus: Map<Int, Boolean>,
//    toggleWatchlist: (Int) -> Unit,
//    showOtherListsPanel: (Boolean) -> Unit,
// ) {
//    var showPopupMenu by remember { mutableStateOf(false) }
//    val color = if (contentInWatchlistStatus.values.contains(true)) {
//        MaterialTheme.colorScheme.secondary
//    } else {
//        MaterialTheme.colorScheme.onPrimary
//    }
//
//    IconButton(
//        onClick = {
//            showPopupMenu = true
//        },
//    ) {
//        Icon(
//            painter = painterResource(resource = Res.drawable.ic_watchlist),
//            contentDescription = null,
//            tint = color,
//        )
//        WatchlistPopUpMenu(
//            showMenu = showPopupMenu,
//            contentInWatchlistStatus = contentInWatchlistStatus,
//            onDismissRequest = {
//                showPopupMenu = false
//            },
//            toggleWatchlist = toggleWatchlist,
//            showCustomLists = { showOtherListsPanel(true) },
//        )
//    }
// }

// @Composable
// fun WatchlistPopUpMenu(
//    showMenu: Boolean,
//    contentInWatchlistStatus: Map<Int, Boolean>,
//    onDismissRequest: () -> Unit,
//    toggleWatchlist: (Int) -> Unit,
//    showCustomLists: () -> Unit,
// ) {
//    val watchlist = stringResource(resource = Res.string.watchlist_tab)
//    val watchlistMenuTitle = if (contentInWatchlistStatus[DefaultLists.WATCHLIST.listId] == true) {
//        stringResource(
//            resource = Res.string.remove_option_popup_menu,
//            watchlist,
//        )
//    } else {
//        stringResource(
//            resource = Res.string.add_option_popup_menu,
//            watchlist,
//        )
//    }
//
//    val watched = stringResource(id = R.string.watched_tab)
//    val watchedMenuTitle = if (contentInWatchlistStatus[DefaultLists.WATCHED.listId] == true) {
//        stringResource(
//            id = R.string.remove_option_popup_menu,
//            watched,
//        )
//    } else {
//        stringResource(
//            id = R.string.add_option_popup_menu,
//            watched,
//        )
//    }
//
//    val menuItems = mutableListOf(
//        PopupMenuItem(
//            title = watchlistMenuTitle,
//            onClick = {
//                toggleWatchlist(DefaultLists.WATCHLIST.listId)
//            },
//        ),
//        PopupMenuItem(
//            title = watchedMenuTitle,
//            onClick = {
//                toggleWatchlist(DefaultLists.WATCHED.listId)
//            },
//        ),
//    )
//
//    if (contentInWatchlistStatus.size > 2) {
//        menuItems.add(
//            PopupMenuItem(
//                title = stringResource(id = R.string.manage_other_lists_popup_menu),
//                onClick = {
//                    showCustomLists()
//                },
//            ),
//        )
//    }
//
//    GenericPopupMenu(
//        showMenu = showMenu,
//        onDismissRequest = onDismissRequest,
//        menuItems = menuItems,
//    )
// }
