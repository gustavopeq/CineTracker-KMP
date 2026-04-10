package navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import common.domain.models.util.MediaType
import common.ui.LocalAnimatedVisibilityScope
import common.ui.LocalSharedTransitionScope
import common.ui.screen.ErrorScreen
import features.auth.ui.AuthScreen
import features.auth.ui.AuthViewModel
import features.auth.ui.EmailAuthScreen
import features.browse.ui.Browse
import features.details.ui.Details
import features.home.ui.Home
import features.search.ui.Search
import features.settings.ui.AvatarPickerScreen
import features.settings.ui.LanguagePickerScreen
import features.settings.ui.RegionPickerScreen
import features.settings.ui.SettingsScreen
import features.watchlist.ui.Watchlist
import navigation.components.navigateToTopLevelDestination
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavGraph(navController: NavHostController) {
    val goToDetails: (Int, MediaType, String, String) -> Unit = { contentId, mediaType, tag, posterPath ->
        navController.navigate(DetailsRoute(contentId, mediaType.name, tag, posterPath))
    }
    val goToErrorScreen: () -> Unit = {
        navController.navigate(ErrorRoute) { launchSingleTop = true }
    }

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
                            goToDetails = goToDetails,
                            goToWatchlist = {
                                navigateToTopLevelDestination(navController, WatchlistRoute)
                            },
                            goToBrowse = {
                                navigateToTopLevelDestination(navController, BrowseRoute)
                            },
                            goToErrorScreen = goToErrorScreen
                        )
                    }
                }
                composable<BrowseRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Browse(
                            goToDetails = goToDetails,
                            goToErrorScreen = goToErrorScreen
                        )
                    }
                }
                composable<WatchlistRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Watchlist(
                            goToDetails = goToDetails,
                            goToErrorScreen = goToErrorScreen
                        )
                    }
                }
                composable<SearchRoute> {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        Search(
                            goToDetails = goToDetails,
                            goToErrorScreen = goToErrorScreen
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
                            goToErrorScreen = goToErrorScreen
                        )
                    }
                }
                navigation<SettingsGraphRoute>(startDestination = SettingsRoute) {
                    composable<SettingsRoute> {
                        SettingsScreen(
                            goToLanguagePicker = { navController.navigate(LanguagePickerRoute) },
                            goToRegionPicker = { navController.navigate(RegionPickerRoute) },
                            goToAvatarPicker = { navController.navigate(AvatarPickerRoute) },
                            goToAuth = { navController.navigate(AuthGraphRoute) }
                        )
                    }
                    composable<LanguagePickerRoute> {
                        LanguagePickerScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<RegionPickerRoute> {
                        RegionPickerScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<AvatarPickerRoute> {
                        AvatarPickerScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                navigation<AuthGraphRoute>(startDestination = AuthRoute) {
                    composable<AuthRoute> {
                        val parentEntry = remember(it) {
                            navController.getBackStackEntry(AuthGraphRoute)
                        }
                        val authViewModel: AuthViewModel =
                            koinViewModel(viewModelStoreOwner = parentEntry)
                        AuthScreen(
                            viewModel = authViewModel,
                            goToEmailAuth = { navController.navigate(EmailAuthRoute) },
                            onDismiss = {
                                navController.popBackStack(AuthGraphRoute, inclusive = true)
                            },
                            onAuthSuccess = {
                                navController.popBackStack(AuthGraphRoute, inclusive = true)
                            }
                        )
                    }
                    composable<EmailAuthRoute> {
                        val parentEntry = remember(it) {
                            navController.getBackStackEntry(AuthGraphRoute)
                        }
                        val authViewModel: AuthViewModel =
                            koinViewModel(viewModelStoreOwner = parentEntry)
                        EmailAuthScreen(
                            viewModel = authViewModel,
                            onBack = { navController.popBackStack() },
                            onAuthSuccess = {
                                navController.popBackStack(AuthGraphRoute, inclusive = true)
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
