package features.watchlist

import navigation.Screen

object WatchlistScreen : Screen {
    private const val WATCHLIST_ROUTE = "watchlist"
    override fun route(): String = WATCHLIST_ROUTE
}
