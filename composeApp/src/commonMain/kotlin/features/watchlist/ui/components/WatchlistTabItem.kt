package features.watchlist.ui.components

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.watched_tab
import cinetracker_kmp.composeapp.generated.resources.watchlist_tab
import common.ui.components.tab.TabItem
import common.util.Constants.ADD_NEW_TAB_ID
import common.util.Constants.UNSELECTED_OPTION_INDEX
import features.watchlist.ui.model.DefaultLists
import org.jetbrains.compose.resources.StringResource

sealed class WatchlistTabItem(
    override val tabResId: StringResource? = null,
    override val tabName: String? = "",
    override var tabIndex: Int = UNSELECTED_OPTION_INDEX,
    open val listId: Int,
) : TabItem {
    data object WatchlistTab : WatchlistTabItem(
        tabResId = Res.string.watchlist_tab,
        listId = DefaultLists.WATCHLIST.listId,
    )
    data object WatchedTab : WatchlistTabItem(
        tabResId = Res.string.watched_tab,
        listId = DefaultLists.WATCHED.listId,
    )
    data object AddNewTab : WatchlistTabItem(
        tabResId = null,
        listId = DefaultLists.ADD_NEW.listId,
        tabIndex = ADD_NEW_TAB_ID,
    )
    data class CustomTab(
        override val tabResId: StringResource? = null,
        override val tabName: String?,
        override var tabIndex: Int = UNSELECTED_OPTION_INDEX,
        override var listId: Int,
    ) : WatchlistTabItem(tabResId, tabName, tabIndex, listId)
}
