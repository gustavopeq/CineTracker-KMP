package navigation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import common.ui.screen.ErrorScreen
import features.browse.ui.Browse
import features.details.DetailsScreen
import navigation.ScreenUI

class BrowseScreenUI : ScreenUI {
    @Composable
    override fun UI(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = currentBackStackEntry?.destination?.route

        Browse(
            goToDetails = { contentId, mediaType ->
                navController.navigate(
                    DetailsScreen.routeWithArguments(contentId, mediaType.name),
                )
            },
            goToErrorScreen = {
                if (currentScreen != ErrorScreen.route()) {
                    navController.navigate(ErrorScreen.route())
                }
            },
        )
    }
}
