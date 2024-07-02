package features.watchlist.ui.state

import common.domain.models.util.SnackbarState
import features.watchlist.ui.model.WatchlistItemAction

data class WatchlistSnackbarState(
    val listId: Int? = null,
    val itemAction: WatchlistItemAction = WatchlistItemAction.ITEM_REMOVED,
) : SnackbarState()
