package navigation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import features.details.DetailsScreen
import features.watchlist.ui.Watchlist
import navigation.ScreenUI

class WatchlistScreenUI : ScreenUI {
    @Composable
    override fun UI(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = currentBackStackEntry?.destination?.route

        Watchlist(
            goToDetails = { contentId, mediaType ->
                navController.navigate(
                    DetailsScreen.routeWithArguments(contentId, mediaType.name),
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
