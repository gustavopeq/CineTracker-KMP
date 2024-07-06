package navigation.components

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
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.app_logo_image_description
import cinetracker_kmp.composeapp.generated.resources.cinetracker_name_logo
import common.ui.MainViewModel
import common.ui.components.button.SortIconButton
import common.util.UiConstants.SMALLER_DEVICES_WIDTH
import common.util.platform.getScreenSizeInfo
import features.browse.BrowseScreen
import features.home.HomeScreen
import features.search.SearchScreen
import features.watchlist.WatchlistScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    currentScreen: String?,
    mainViewModel: MainViewModel,
    displaySortScreen: (Boolean) -> Unit,
) {
    val title = currentScreen.getScreenNameRes()?.let { stringResource(resource = it) }
    val showTopBar = screensWithTopBar.contains(currentScreen)
    val logoModifier = if (getScreenSizeInfo().widthDp < SMALLER_DEVICES_WIDTH.dp) {
        Modifier.fillMaxWidth(0.5f)
    } else {
        Modifier
    }

    println("width: ${getScreenSizeInfo().widthDp} - ${getScreenSizeInfo().widthPx}")
    if (showTopBar) {
        TopAppBar(
            title = {
                if (currentScreen == HomeScreen.route()) {
                    Image(
                        modifier = logoModifier,
                        painter = painterResource(resource = Res.drawable.cinetracker_name_logo),
                        contentDescription = stringResource(
                            resource = Res.string.app_logo_image_description,
                        ),
                    )
                } else {
                    Text(
                        text = title.orEmpty(),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            },
            actions = {
                if (screensWithSortIcon.contains(currentScreen)) {
                    SortIconButton(
                        mainViewModel = mainViewModel,
                        currentScreen = currentScreen.orEmpty(),
                        displaySortScreen = displaySortScreen,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
    }
}

private fun String?.getScreenNameRes(): StringResource? {
    return when (this) {
        HomeScreen.route() -> MainNavBarItem.Home.labelResId
        BrowseScreen.route() -> MainNavBarItem.Browse.labelResId
        WatchlistScreen.route() -> MainNavBarItem.Watchlist.labelResId
        SearchScreen.route() -> MainNavBarItem.Search.labelResId
        else -> null
    }
}

private val screensWithTopBar = listOf(
    HomeScreen.route(),
    BrowseScreen.route(),
    WatchlistScreen.route(),
)

private val screensWithSortIcon = listOf(
    BrowseScreen.route(),
    WatchlistScreen.route(),
)
