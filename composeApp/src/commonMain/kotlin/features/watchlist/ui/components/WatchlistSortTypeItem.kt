package features.watchlist.ui.components

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.movie_tag
import cinetracker_kmp.composeapp.generated.resources.show_tag
import common.domain.models.util.MediaType
import org.jetbrains.compose.resources.StringResource

sealed class WatchlistSortTypeItem(
    val titleRes: StringResource,
    val mediaType: MediaType,
) {
    data object MovieOnly : WatchlistSortTypeItem(
        titleRes = Res.string.movie_tag,
        mediaType = MediaType.MOVIE,
    )
    data object ShowOnly : WatchlistSortTypeItem(
        titleRes = Res.string.show_tag,
        mediaType = MediaType.SHOW,
    )
}
