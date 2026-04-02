package navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import common.ui.LocalAnimatedVisibilityScope
import common.ui.LocalSharedTransitionScope
import common.ui.screen.ErrorScreen
import features.browse.ui.Browse
import features.details.ui.Details
import features.home.ui.Home
import features.search.ui.Search
import features.watchlist.ui.Watchlist
import navigation.components.navigateToTopLevelDestination

@Composable
fun MainNavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable<HomeRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Home(
                            goToDetails = { contentId, mediaType, tag, posterPath ->
                                navController.navigate(DetailsRoute(contentId, mediaType.name, tag, posterPath))
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
                }
                composable<BrowseRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Browse(
                            goToDetails = { contentId, mediaType, tag, posterPath ->
                                navController.navigate(DetailsRoute(contentId, mediaType.name, tag, posterPath))
                            },
                            goToErrorScreen = {
                                navController.navigate(ErrorRoute) { launchSingleTop = true }
                            }
                        )
                    }
                }
                composable<WatchlistRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Watchlist(
                            goToDetails = { contentId, mediaType, tag, posterPath ->
                                navController.navigate(DetailsRoute(contentId, mediaType.name, tag, posterPath))
                            },
                            goToErrorScreen = {
                                navController.navigate(ErrorRoute) { launchSingleTop = true }
                            }
                        )
                    }
                }
                composable<SearchRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Search(
                            goToDetails = { contentId, mediaType, tag, posterPath ->
                                navController.navigate(DetailsRoute(contentId, mediaType.name, tag, posterPath))
                            },
                            goToErrorScreen = {
                                navController.navigate(ErrorRoute) { launchSingleTop = true }
                            }
                        )
                    }
                }
                composable<DetailsRoute>(
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() }
                ) { backStackEntry ->
                    val route = backStackEntry.toRoute<DetailsRoute>()
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Details(
                            contentId = route.contentId,
                            mediaType = route.mediaType,
                            sharedElementTag = route.sharedElementTag,
                            posterPath = route.posterPath,
                            onBackPress = { navController.popBackStack() },
                            goToDetails = { contentId, mediaType ->
                                navController.navigate(DetailsRoute(contentId, mediaType.name))
                            },
                            goToErrorScreen = {
                                navController.navigate(ErrorRoute) { launchSingleTop = true }
                            }
                        )
                    }
                }
                composable<ErrorRoute> {
                    ErrorScreen(onTryAgain = { navController.popBackStack() })
                }
            }
        }
    }
}
