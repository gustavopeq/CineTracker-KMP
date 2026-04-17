
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import auth.platform.RecoveryHandler
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import common.ui.MainViewModel
import common.ui.theme.CineTrackerTheme
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor
import core.getAsyncImageLoader
import features.auth.ui.AccountAnnouncementView
import features.onboarding.ui.OnboardingView
import navigation.AuthGraphRoute
import navigation.MainScaffoldRoute
import navigation.NewPasswordRoute
import navigation.RootNavGraph
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
    val rootNavController = rememberNavController()

    val pendingAuthNavigation by mainViewModel.pendingAuthNavigation.collectAsState()
    LaunchedEffect(pendingAuthNavigation) {
        if (pendingAuthNavigation) {
            rootNavController.navigate(AuthGraphRoute)
            mainViewModel.onAuthNavigationHandled()
        }
    }

    val pendingRecoveryToken by RecoveryHandler.pendingRecoveryToken.collectAsState()
    LaunchedEffect(pendingRecoveryToken) {
        if (pendingRecoveryToken != null) {
            rootNavController.navigate(NewPasswordRoute) {
                popUpTo(MainScaffoldRoute) { inclusive = false }
            }
        }
    }

    RootNavGraph(rootNavController)
}

@Composable
fun SystemBarsContainer(currentDestination: NavDestination? = null, appScaffold: @Composable () -> Unit) {
    val statusBarColor = MaterialTheme.colorScheme.primary

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
                .background(color = MainBarGreyColor)
        )

        appScaffold()
    }
}
