package navigation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import common.ui.screen.ErrorScreen
import features.details.DetailsScreen
import features.details.ui.Details
import navigation.ScreenUI

class DetailsScreenUI : ScreenUI {
    @Composable
    override fun UI(navController: NavController) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentScreen = currentBackStackEntry?.destination?.route

        val backStackEntry = remember {
            navController.getBackStackEntry(DetailsScreen.route())
        }

        Details(
            navBackStackEntry = backStackEntry,
            onBackPress = {
                navController.popBackStack()
            },
            goToDetails = { id, type ->
                navController.navigate(
                    DetailsScreen.routeWithArguments(id, type.name),
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
