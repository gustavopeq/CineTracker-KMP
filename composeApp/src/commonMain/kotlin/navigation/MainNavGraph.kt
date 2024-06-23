package navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import features.browse.BrowseScreen
import features.home.HomeScreen
import features.search.SearchScreen
import features.watchlist.WatchlistScreen
import navigation.screens.BrowseScreenUI
import navigation.screens.HomeScreenUI
import navigation.screens.SearchScreenUI
import navigation.screens.WatchlistScreenUI

private val mainNavDestinations: Map<Screen, ScreenUI> = mapOf(
    HomeScreen to HomeScreenUI(),
    BrowseScreen to BrowseScreenUI(),
    WatchlistScreen to WatchlistScreenUI(),
    SearchScreen to SearchScreenUI(),
)

@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.route(),
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        mainNavDestinations.forEach { (screen, screenUI) ->
            composable(screen.route(), screen.arguments) {
                screenUI.UI(
                    navController = navController,
                )
            }
        }
    }
}
