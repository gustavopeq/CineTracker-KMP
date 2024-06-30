package navigation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import features.browse.BrowseScreen
import features.details.DetailsScreen
import features.home.ui.Home
import navigation.ScreenUI
import navigation.components.navigateToTopLevelDestination

class HomeScreenUI : ScreenUI {
    @Composable
    override fun UI(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = currentBackStackEntry?.destination?.route

        Home(
            goToDetails = { contentId, mediaType ->
                navController.navigate(
                    DetailsScreen.routeWithArguments(contentId, mediaType.name),
                )
            },
            goToWatchlist = {
//                navigateToTopLevelDestination(
//                    navController = navController,
//                    destination = WatchlistScreen.route()
//                )
            },
            goToBrowse = {
                navigateToTopLevelDestination(
                    navController = navController,
                    destination = BrowseScreen.route(),
                )
            },
            goToErrorScreen = {
//                if (currentScreen != ErrorScreen.route()) {
//                    navController.navigate(ErrorScreen.route())
//                }
            },
        )
    }
}
