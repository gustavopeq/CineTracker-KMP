package navigation

import SystemBarsContainer
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import common.domain.models.util.MediaType
import common.ui.LocalAnimatedVisibilityScope
import common.ui.LocalSharedTransitionScope
import common.ui.MainViewModel
import common.ui.components.bottomsheet.ModalComponents
import common.ui.screen.ErrorScreen
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryWhiteColor
import features.auth.ui.AuthScreen
import features.auth.ui.AuthViewModel
import features.auth.ui.EmailAuthScreen
import features.auth.ui.ForgotPasswordScreen
import features.auth.ui.NewPasswordScreen
import features.browse.ui.Browse
import features.details.ui.Details
import features.home.ui.Home
import features.search.ui.Search
import features.settings.ui.AvatarPickerScreen
import features.settings.ui.LanguagePickerScreen
import features.settings.ui.RegionPickerScreen
import features.settings.ui.SettingsScreen
import features.watchlist.ui.Watchlist
import features.watchlist.ui.components.CreateListBottomSheet
import navigation.components.MainNavBar
import navigation.components.MainNavBarItem
import navigation.components.TopNavBar
import navigation.components.navigateToTopLevelDestination
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootNavGraph(rootNavController: NavHostController) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = rootNavController,
                startDestination = MainScaffoldRoute,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable<MainScaffoldRoute>(
                    popEnterTransition = { fadeIn(tween(100)) }
                ) {
                    CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                        MainScaffoldScreen(rootNavController = rootNavController)
                    }
                }
                composable<SearchRoute>(
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                                .background(MainBarGreyColor)
                                .align(Alignment.TopCenter)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(WindowInsets.systemBars)
                        ) {
                            Search(
                                onBackPress = { rootNavController.popBackStack() },
                                goToDetails = { contentId, mediaType, tag, posterPath ->
                                    rootNavController.navigate(
                                        DetailsRoute(contentId, mediaType.name, tag, posterPath)
                                    )
                                },
                                goToErrorScreen = {
                                    rootNavController.navigate(ErrorRoute) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
                composable<DetailsRoute>(
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() }
                ) { backStackEntry ->
                    val route = backStackEntry.toRoute<DetailsRoute>()
                    CompositionLocalProvider(
                        LocalAnimatedVisibilityScope provides this,
                        LocalContentColor provides PrimaryWhiteColor
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PrimaryBlackColor)
                                .windowInsetsPadding(WindowInsets.systemBars)
                        ) {
                            Details(
                                contentId = route.contentId,
                                mediaType = route.mediaType,
                                sharedElementTag = route.sharedElementTag,
                                posterPath = route.posterPath,
                                onBackPress = { rootNavController.popBackStack() },
                                goToDetails = { contentId, mediaType ->
                                    rootNavController.navigate(
                                        DetailsRoute(contentId, mediaType.name)
                                    )
                                },
                                goToErrorScreen = {
                                    rootNavController.navigate(ErrorRoute) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
                navigation<AuthGraphRoute>(startDestination = AuthRoute) {
                    composable<AuthRoute> {
                        val parentEntry = remember(it) {
                            rootNavController.getBackStackEntry(AuthGraphRoute)
                        }
                        val authViewModel: AuthViewModel =
                            koinViewModel(viewModelStoreOwner = parentEntry)
                        AuthScreen(
                            viewModel = authViewModel,
                            goToEmailAuth = {
                                rootNavController.navigate(EmailAuthRoute)
                            },
                            onDismiss = {
                                rootNavController.popBackStack(
                                    AuthGraphRoute,
                                    inclusive = true
                                )
                            },
                            onAuthSuccess = {
                                rootNavController.popBackStack(
                                    AuthGraphRoute,
                                    inclusive = true
                                )
                            }
                        )
                    }
                    composable<EmailAuthRoute> {
                        val parentEntry = remember(it) {
                            rootNavController.getBackStackEntry(AuthGraphRoute)
                        }
                        val authViewModel: AuthViewModel =
                            koinViewModel(viewModelStoreOwner = parentEntry)
                        EmailAuthScreen(
                            viewModel = authViewModel,
                            onBack = { rootNavController.popBackStack() },
                            onAuthSuccess = {
                                rootNavController.popBackStack(
                                    AuthGraphRoute,
                                    inclusive = true
                                )
                            },
                            onForgotPassword = {
                                rootNavController.navigate(ForgotPasswordRoute)
                            }
                        )
                    }
                    composable<ForgotPasswordRoute> {
                        val parentEntry = remember(it) {
                            rootNavController.getBackStackEntry(AuthGraphRoute)
                        }
                        val authViewModel: AuthViewModel =
                            koinViewModel(viewModelStoreOwner = parentEntry)
                        ForgotPasswordScreen(
                            viewModel = authViewModel,
                            onBack = { rootNavController.popBackStack() }
                        )
                    }
                }
                composable<NewPasswordRoute> {
                    NewPasswordScreen(
                        onDone = {
                            rootNavController.popBackStack()
                            rootNavController.navigate(AuthGraphRoute)
                        }
                    )
                }
                composable<ErrorRoute> {
                    CompositionLocalProvider(LocalContentColor provides PrimaryWhiteColor) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PrimaryBlackColor)
                                .windowInsetsPadding(WindowInsets.systemBars)
                        ) {
                            ErrorScreen(onTryAgain = { rootNavController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScaffoldScreen(rootNavController: NavHostController) {
    val nestedNavController = rememberNavController()
    val nestedBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = nestedBackStackEntry?.destination
        ?: nestedNavController.currentDestination

    val mainViewModel: MainViewModel = koinViewModel()

    var showSortBottomSheet by remember { mutableStateOf(false) }
    val displaySortScreen: (Boolean) -> Unit = { showSortBottomSheet = it }

    SystemBarsContainer {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopNavBar(
                    currentDestination = currentDestination,
                    mainViewModel = mainViewModel,
                    displaySortScreen = displaySortScreen,
                    goToSearch = { rootNavController.navigate(SearchRoute) }
                )
            },
            bottomBar = {
                MainNavBar(
                    navController = nestedNavController,
                    navBarItems = mainNavBarItems
                )
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NestedNavGraph(
                        nestedNavController = nestedNavController,
                        rootNavController = rootNavController
                    )
                }
            }
        )
    }

    ModalComponents(
        mainViewModel = mainViewModel,
        currentDestination = currentDestination,
        showSortBottomSheet = showSortBottomSheet,
        displaySortScreen = displaySortScreen
    )

    CreateListBottomSheet(
        mainViewModel = mainViewModel
    )
}

@Composable
private fun NestedNavGraph(nestedNavController: NavHostController, rootNavController: NavHostController) {
    val goToDetails: (Int, MediaType, String, String) -> Unit =
        { contentId, mediaType, tag, posterPath ->
            rootNavController.navigate(
                DetailsRoute(contentId, mediaType.name, tag, posterPath)
            )
        }
    val goToErrorScreen: () -> Unit = {
        rootNavController.navigate(ErrorRoute) { launchSingleTop = true }
    }

    NavHost(
        navController = nestedNavController,
        startDestination = HomeRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<HomeRoute> {
            Home(
                goToDetails = goToDetails,
                goToWatchlist = {
                    navigateToTopLevelDestination(nestedNavController, WatchlistRoute)
                },
                goToBrowse = {
                    navigateToTopLevelDestination(nestedNavController, BrowseRoute)
                },
                goToErrorScreen = goToErrorScreen
            )
        }
        composable<BrowseRoute> {
            Browse(
                goToDetails = goToDetails,
                goToErrorScreen = goToErrorScreen
            )
        }
        composable<WatchlistRoute> {
            Watchlist(
                goToDetails = goToDetails,
                goToErrorScreen = goToErrorScreen
            )
        }
        navigation<SettingsGraphRoute>(startDestination = SettingsRoute) {
            composable<SettingsRoute> {
                SettingsScreen(
                    goToLanguagePicker = { nestedNavController.navigate(LanguagePickerRoute) },
                    goToRegionPicker = { nestedNavController.navigate(RegionPickerRoute) },
                    goToAvatarPicker = { nestedNavController.navigate(AvatarPickerRoute) },
                    goToAuth = { rootNavController.navigate(AuthGraphRoute) }
                )
            }
            composable<LanguagePickerRoute> {
                LanguagePickerScreen(
                    onBack = { nestedNavController.popBackStack() }
                )
            }
            composable<RegionPickerRoute> {
                RegionPickerScreen(
                    onBack = { nestedNavController.popBackStack() }
                )
            }
            composable<AvatarPickerRoute> {
                AvatarPickerScreen(
                    onBack = { nestedNavController.popBackStack() }
                )
            }
        }
    }
}

private val mainNavBarItems = listOf(
    MainNavBarItem.Home,
    MainNavBarItem.Browse,
    MainNavBarItem.Watchlist,
    MainNavBarItem.Settings
)
