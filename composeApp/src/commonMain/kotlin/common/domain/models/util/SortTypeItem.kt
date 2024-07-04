package common.domain.models.util

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.now_playing
import cinetracker_kmp.composeapp.generated.resources.popular_movies
import cinetracker_kmp.composeapp.generated.resources.show_airing_today
import cinetracker_kmp.composeapp.generated.resources.show_on_the_air
import cinetracker_kmp.composeapp.generated.resources.show_popular
import cinetracker_kmp.composeapp.generated.resources.show_top_rated
import cinetracker_kmp.composeapp.generated.resources.top_rated_movies
import cinetracker_kmp.composeapp.generated.resources.upcoming_movies
import org.jetbrains.compose.resources.StringResource

sealed class SortTypeItem(
    val titleRes: StringResource,
    val listType: ContentListType,
    val itemIndex: Int,
) {
    // Movies sorting
    data object NowPlaying : SortTypeItem(
        titleRes = Res.string.now_playing,
        listType = ContentListType.MOVIE_NOW_PLAYING,
        itemIndex = 0,
    )
    data object Popular : SortTypeItem(
        titleRes = Res.string.popular_movies,
        listType = ContentListType.MOVIE_POPULAR,
        itemIndex = 1,
    )
    data object TopRated : SortTypeItem(
        titleRes = Res.string.top_rated_movies,
        listType = ContentListType.MOVIE_TOP_RATED,
        itemIndex = 2,
    )
    data object Upcoming : SortTypeItem(
        titleRes = Res.string.upcoming_movies,
        listType = ContentListType.MOVIE_UPCOMING,
        itemIndex = 3,
    )

    // Shows sorting
    data object AiringToday : SortTypeItem(
        titleRes = Res.string.show_airing_today,
        listType = ContentListType.SHOW_AIRING_TODAY,
        itemIndex = 0,
    )
    data object ShowPopular : SortTypeItem(
        titleRes = Res.string.show_popular,
        listType = ContentListType.SHOW_POPULAR,
        itemIndex = 1,
    )
    data object ShowTopRated : SortTypeItem(
        titleRes = Res.string.show_top_rated,
        listType = ContentListType.SHOW_TOP_RATED,
        itemIndex = 2,
    )
    data object OnTheAir : SortTypeItem(
        titleRes = Res.string.show_on_the_air,
        listType = ContentListType.SHOW_ON_THE_AIR,
        itemIndex = 3,
    )
}
