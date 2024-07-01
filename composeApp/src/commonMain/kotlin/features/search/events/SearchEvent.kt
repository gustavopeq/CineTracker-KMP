package features.search.events

import features.search.ui.components.SearchTypeFilterItem

sealed class SearchEvent {
    data object ClearSearchBar : SearchEvent()
    data object OnError : SearchEvent()
    data class SearchQuery(
        val query: String,
    ) : SearchEvent()
    data class FilterTypeSelected(
        val searchFilter: SearchTypeFilterItem,
    ) : SearchEvent()
}
