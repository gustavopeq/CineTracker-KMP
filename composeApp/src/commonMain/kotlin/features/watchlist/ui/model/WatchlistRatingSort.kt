package features.watchlist.ui.model

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.watchlist_sort_personal_rating
import cinetracker_kmp.composeapp.generated.resources.watchlist_sort_public_rating
import org.jetbrains.compose.resources.StringResource

sealed class WatchlistRatingSort(val titleRes: StringResource) {
    data object PublicRating : WatchlistRatingSort(
        titleRes = Res.string.watchlist_sort_public_rating
    )
    data object PersonalRating : WatchlistRatingSort(
        titleRes = Res.string.watchlist_sort_personal_rating
    )
}
