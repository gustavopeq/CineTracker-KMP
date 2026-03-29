package navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object BrowseRoute

@Serializable
data object WatchlistRoute

@Serializable
data object SearchRoute

@Serializable
data class DetailsRoute(val contentId: Int, val mediaType: String)

@Serializable
data object ErrorRoute
