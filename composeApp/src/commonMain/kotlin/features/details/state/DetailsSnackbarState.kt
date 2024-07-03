package features.details.state

import common.domain.models.util.SnackbarState
import features.watchlist.ui.model.DefaultLists

data class DetailsSnackbarState(
    val listId: Int = DefaultLists.WATCHLIST.listId,
    val addedItem: Boolean = false,
) : SnackbarState()
