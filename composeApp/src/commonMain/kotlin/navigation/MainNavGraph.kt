package navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import common.ui.screen.ErrorScreen
import features.browse.ui.Browse
import features.details.ui.Details
import features.home.ui.Home
import features.search.ui.Search
import features.watchlist.ui.Watchlist
import navigation.components.navigateToTopLevelDestination

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<HomeRoute> {
            Home(
                goToDetails = { contentId, mediaType ->
                    navController.navigate(DetailsRoute(contentId, mediaType.name))
                },
                goToWatchlist = {
                    navigateToTopLevelDestination(navController, WatchlistRoute)
                },
                goToBrowse = {
                    navigateToTopLevelDestination(navController, BrowseRoute)
                },
                goToErrorScreen = {
                    navController.navigate(ErrorRoute) { launchSingleTop = true }
                }
            )
        }
        composable<BrowseRoute> {
            Browse(
                goToDetails = { contentId, mediaType ->
                    navController.navigate(DetailsRoute(contentId, mediaType.name))
                },
                goToErrorScreen = {
                    navController.navigate(ErrorRoute) { launchSingleTop = true }
                }
            )
        }
        composable<WatchlistRoute> {
            Watchlist(
                goToDetails = { contentId, mediaType ->
                    navController.navigate(DetailsRoute(contentId, mediaType.name))
                },
                goToErrorScreen = {
                    navController.navigate(ErrorRoute) { launchSingleTop = true }
                }
            )
        }
        composable<SearchRoute> {
            Search(
                goToDetails = { contentId, mediaType ->
                    navController.navigate(DetailsRoute(contentId, mediaType.name))
                },
                goToErrorScreen = {
                    navController.navigate(ErrorRoute) { launchSingleTop = true }
                }
            )
        }
        composable<DetailsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailsRoute>()
            Details(
                contentId = route.contentId,
                mediaType = route.mediaType,
                onBackPress = { navController.popBackStack() },
                goToDetails = { contentId, mediaType ->
                    navController.navigate(DetailsRoute(contentId, mediaType.name))
                },
                goToErrorScreen = {
                    navController.navigate(ErrorRoute) { launchSingleTop = true }
                }
            )
        }
        composable<ErrorRoute> {
            ErrorScreen(onTryAgain = { navController.popBackStack() })
        }
    }
}
