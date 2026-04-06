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
data class DetailsRoute(
    val contentId: Int,
    val mediaType: String,
    val sharedElementTag: String = "",
    val posterPath: String = ""
)

@Serializable
data object SettingsRoute

@Serializable
data object LanguagePickerRoute

@Serializable
data object RegionPickerRoute

@Serializable
data object ErrorRoute
