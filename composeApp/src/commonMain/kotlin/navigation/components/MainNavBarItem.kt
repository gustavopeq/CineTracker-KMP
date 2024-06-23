package navigation.components

import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_nav_browse
import cinetracker_kmp.composeapp.generated.resources.ic_nav_home
import cinetracker_kmp.composeapp.generated.resources.ic_nav_search
import cinetracker_kmp.composeapp.generated.resources.ic_nav_watchlist
import cinetracker_kmp.composeapp.generated.resources.main_nav_browse
import cinetracker_kmp.composeapp.generated.resources.main_nav_home
import cinetracker_kmp.composeapp.generated.resources.main_nav_search
import cinetracker_kmp.composeapp.generated.resources.main_nav_watchlist
import features.browse.BrowseScreen
import features.home.HomeScreen
import features.search.SearchScreen
import features.watchlist.WatchlistScreen
import navigation.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class MainNavBarItem(
    val screen: Screen,
    val labelResId: StringResource,
    val iconResId: DrawableResource,
) {
    data object Home : MainNavBarItem(
        screen = HomeScreen,
        labelResId = Res.string.main_nav_home,
        iconResId = Res.drawable.ic_nav_home,
    )
    data object Browse : MainNavBarItem(
        screen = BrowseScreen,
        labelResId = Res.string.main_nav_browse,
        iconResId = Res.drawable.ic_nav_browse,
    )
    data object Watchlist : MainNavBarItem(
        screen = WatchlistScreen,
        labelResId = Res.string.main_nav_watchlist,
        iconResId = Res.drawable.ic_nav_watchlist,
    )
    data object Search : MainNavBarItem(
        screen = SearchScreen,
        labelResId = Res.string.main_nav_search,
        iconResId = Res.drawable.ic_nav_search,
    )
}
