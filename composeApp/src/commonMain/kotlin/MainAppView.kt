
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import common.ui.MainViewModel
import common.ui.components.bottomsheet.ModalComponents
import common.ui.theme.CineTrackerTheme
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor
import core.getAsyncImageLoader
import features.announcement.ui.AccountAnnouncementView
import features.onboarding.ui.OnboardingView
import features.watchlist.ui.components.CreateListBottomSheet
import navigation.AuthGraphRoute
import navigation.AuthRoute
import navigation.DetailsRoute
import navigation.EmailAuthRoute
import navigation.ErrorRoute
import navigation.MainNavGraph
import navigation.SearchRoute
import navigation.components.MainNavBar
import navigation.components.MainNavBarItem
import navigation.components.TopNavBar
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MainAppView() {
    CineTrackerTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        val mainViewModel: MainViewModel = koinViewModel()
        val hasSeenOnboarding by mainViewModel.hasSeenOnboarding.collectAsState()

        when (hasSeenOnboarding) {
            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryBlackColor)
                )
            }
            false -> {
                OnboardingView(
                    onOnboardingComplete = { mainViewModel.updateOnboardingUiState() }
                )
            }
            true -> {
                val shouldShowAnnouncement by mainViewModel.shouldShowAnnouncement.collectAsState()
                if (shouldShowAnnouncement) {
                    AccountAnnouncementView(
                        onCreateAccount = { mainViewModel.onAnnouncementCreateAccount() },
                        onDismiss = { mainViewModel.onAnnouncementDismiss() }
                    )
                } else {
                    MainAppContent(mainViewModel)
                }
            }
        }
    }
}

@Composable
private fun MainAppContent(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    val pendingAuthNavigation by mainViewModel.pendingAuthNavigation.collectAsState()
    LaunchedEffect(pendingAuthNavigation) {
        if (pendingAuthNavigation) {
            navController.navigate(AuthGraphRoute)
            mainViewModel.onAuthNavigationHandled()
        }
    }

    val navItems = mainNavBarItems
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val isStandaloneScreen = currentDestination.isStandalone()

    var showSortBottomSheet by remember { mutableStateOf(false) }

    val displaySortScreen: (Boolean) -> Unit = {
        showSortBottomSheet = it
    }

    SystemBarsContainer(
        currentDestination = currentDestination
    ) {
        Scaffold(
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopNavBar(
                    currentDestination = currentDestination,
                    mainViewModel = mainViewModel,
                    displaySortScreen = displaySortScreen
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = !isStandaloneScreen,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)),
                    exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
                ) {
                    MainNavBar(
                        navController = navController,
                        navBarItems = navItems
                    )
                }
            },
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    MainNavGraph(navController)
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
fun SystemBarsContainer(currentDestination: NavDestination? = null, appScaffold: @Composable () -> Unit) {
    val isStandaloneScreen = currentDestination.isStandalone()

    val statusBarColor = when {
        currentDestination?.hasRoute<SearchRoute>() == true -> MainBarGreyColor
        else -> MaterialTheme.colorScheme.primary
    }

    val navigationBarColor = if (isStandaloneScreen) {
        MaterialTheme.colorScheme.primary
    } else {
        MainBarGreyColor
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .align(Alignment.TopCenter)
                .background(color = statusBarColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .align(Alignment.BottomCenter)
                .background(color = navigationBarColor)
        )

        appScaffold()
    }
}

private fun NavDestination?.isStandalone(): Boolean = this?.hasRoute<DetailsRoute>() == true ||
    this?.hasRoute<ErrorRoute>() == true ||
    this?.hasRoute<AuthRoute>() == true ||
    this?.hasRoute<EmailAuthRoute>() == true

val mainNavBarItems = listOf<MainNavBarItem>(
    MainNavBarItem.Home,
    MainNavBarItem.Browse,
    MainNavBarItem.Watchlist,
    MainNavBarItem.Search,
    MainNavBarItem.Settings
)
