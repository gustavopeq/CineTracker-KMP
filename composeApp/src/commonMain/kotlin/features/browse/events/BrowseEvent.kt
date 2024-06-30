package features.browse.events

import common.domain.models.util.MediaType
import common.domain.util.SortTypeItem

sealed class BrowseEvent {
    data class UpdateSortType(
        val movieListType: SortTypeItem,
        val mediaType: MediaType,
    ) : BrowseEvent()
    data class UpdateMediaType(
        val mediaType: MediaType,
    ) : BrowseEvent()
    data object OnError : BrowseEvent()
}
