package features.watchlist.util

import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import features.watchlist.ui.state.WatchlistState

fun fakeGenericContent(id: Int = 1, name: String = "Test Movie", mediaType: MediaType = MediaType.MOVIE) =
    GenericContent(
        id = id,
        name = name,
        rating = 7.5,
        overview = "Overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        mediaType = mediaType
    )

fun successfulWatchlistState(vararg items: GenericContent): WatchlistState =
    WatchlistState().apply { listItems.value = items.toList() }
