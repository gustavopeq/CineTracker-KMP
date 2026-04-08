package navigation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.app_logo_image_description
import cinetracker_kmp.composeapp.generated.resources.cinetracker_name_logo
import common.ui.MainViewModel
import common.ui.components.button.SortIconButton
import common.util.UiConstants.SMALLER_DEVICES_WIDTH
import common.util.platform.getScreenSizeInfo
import navigation.BrowseRoute
import navigation.HomeRoute
import navigation.SettingsRoute
import navigation.WatchlistRoute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(currentDestination: NavDestination?, mainViewModel: MainViewModel, displaySortScreen: (Boolean) -> Unit) {
    val isHomeScreen = currentDestination?.hasRoute<HomeRoute>() == true
    val isBrowseScreen = currentDestination?.hasRoute<BrowseRoute>() == true
    val isWatchlistScreen = currentDestination?.hasRoute<WatchlistRoute>() == true
    val isSettingsScreen = currentDestination?.hasRoute<SettingsRoute>() == true

    val showTopBar = isHomeScreen || isBrowseScreen || isWatchlistScreen || isSettingsScreen
    val showSortIcon = isBrowseScreen || isWatchlistScreen

    val title = when {
        isHomeScreen -> null
        isBrowseScreen -> stringResource(resource = MainNavBarItem.Browse.labelResId)
        isWatchlistScreen -> stringResource(resource = MainNavBarItem.Watchlist.labelResId)
        isSettingsScreen -> stringResource(resource = MainNavBarItem.Settings.labelResId)
        else -> null
    }

    val logoModifier = if (getScreenSizeInfo().widthDp < SMALLER_DEVICES_WIDTH.dp) {
        Modifier.fillMaxWidth(0.5f)
    } else {
        Modifier
    }

    AnimatedVisibility(
        visible = showTopBar,
        enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)),
        exit = ExitTransition.None
    ) {
        TopAppBar(
            title = {
                if (isHomeScreen) {
                    Image(
                        modifier = logoModifier,
                        painter = painterResource(resource = Res.drawable.cinetracker_name_logo),
                        contentDescription = stringResource(
                            resource = Res.string.app_logo_image_description
                        )
                    )
                } else {
                    Text(
                        text = title.orEmpty(),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            },
            actions = {
                if (showSortIcon) {
                    SortIconButton(
                        mainViewModel = mainViewModel,
                        isWatchlistScreen = isWatchlistScreen,
                        displaySortScreen = displaySortScreen
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}
