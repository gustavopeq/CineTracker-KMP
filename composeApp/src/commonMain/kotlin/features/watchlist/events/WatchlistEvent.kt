package features.watchlist.events

import common.domain.models.util.MediaType
import features.watchlist.ui.components.WatchlistTabItem

sealed class WatchlistEvent {
    data object LoadWatchlistData : WatchlistEvent()
    data object OnSnackbarDismiss : WatchlistEvent()
    data object UndoItemAction : WatchlistEvent()
    data class RemoveItem(
        val contentId: Int,
        val mediaType: MediaType,
    ) : WatchlistEvent()
    data class SelectList(
        val tabItem: WatchlistTabItem,
    ) : WatchlistEvent()
    data class UpdateSortType(
        val mediaType: MediaType?,
    ) : WatchlistEvent()
    data class UpdateItemListId(
        val contentId: Int,
        val mediaType: MediaType,
        val listId: Int,
    ) : WatchlistEvent()
    data object LoadAllLists : WatchlistEvent()
    data class DeleteList(
        val listId: Int,
    ) : WatchlistEvent()
}
