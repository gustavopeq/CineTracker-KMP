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
import navigation.BrowseRoute
import navigation.HomeRoute
import navigation.SearchRoute
import navigation.WatchlistRoute
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class MainNavBarItem(val route: Any, val labelResId: StringResource, val iconResId: DrawableResource) {
    data object Home : MainNavBarItem(
        route = HomeRoute,
        labelResId = Res.string.main_nav_home,
        iconResId = Res.drawable.ic_nav_home
    )
    data object Browse : MainNavBarItem(
        route = BrowseRoute,
        labelResId = Res.string.main_nav_browse,
        iconResId = Res.drawable.ic_nav_browse
    )
    data object Watchlist : MainNavBarItem(
        route = WatchlistRoute,
        labelResId = Res.string.main_nav_watchlist,
        iconResId = Res.drawable.ic_nav_watchlist
    )
    data object Search : MainNavBarItem(
        route = SearchRoute,
        labelResId = Res.string.main_nav_search,
        iconResId = Res.drawable.ic_nav_search
    )
}
