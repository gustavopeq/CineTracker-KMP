package navigation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import common.ui.theme.MainBarGreyColor
import common.util.UiConstants.BUTTON_NAVIGATION_BAR_HEIGHT
import common.util.platform.AppHaptics
import navigation.BrowseRoute
import navigation.HomeRoute
import navigation.SearchRoute
import navigation.WatchlistRoute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainNavBar(navController: NavController, navBarItems: List<MainNavBarItem>) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(BUTTON_NAVIGATION_BAR_HEIGHT.dp),
        containerColor = MainBarGreyColor
    ) {
        navBarItems.forEach { item ->
            val isSelected = when (item) {
                is MainNavBarItem.Home -> currentDestination?.hasRoute<HomeRoute>() == true
                is MainNavBarItem.Browse -> currentDestination?.hasRoute<BrowseRoute>() == true
                is MainNavBarItem.Watchlist -> currentDestination?.hasRoute<WatchlistRoute>() == true
                is MainNavBarItem.Search -> currentDestination?.hasRoute<SearchRoute>() == true
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        AppHaptics.light()
                        navigateToTopLevelDestination(
                            navController = navController,
                            destination = item.route
                        )
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(resource = item.iconResId),
                            contentDescription = stringResource(resource = item.labelResId)
                        )
                        Text(
                            text = stringResource(resource = item.labelResId),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MainBarGreyColor,
                    selectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

fun navigateToTopLevelDestination(navController: NavController, destination: Any) {
    navController.navigate(destination) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
