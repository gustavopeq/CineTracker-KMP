package common.ui.components.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import common.ui.MainViewModel
import features.browse.ui.components.BrowseSortBottomSheet
import features.watchlist.ui.components.WatchlistSortBottomSheet
import navigation.BrowseRoute
import navigation.WatchlistRoute

@Composable
fun ModalComponents(
    mainViewModel: MainViewModel,
    currentDestination: NavDestination?,
    showSortBottomSheet: Boolean,
    displaySortScreen: (Boolean) -> Unit
) {
    val selectedMovieSortType by mainViewModel.movieSortType.collectAsState()
    val selectedShowSortType by mainViewModel.showSortType.collectAsState()
    val selectedMediaType by mainViewModel.currentMediaTypeSelected.collectAsState()

    if (showSortBottomSheet) {
        when {
            currentDestination?.hasRoute<BrowseRoute>() == true -> {
                BrowseSortBottomSheet(
                    mainViewModel = mainViewModel,
                    selectedMovieSortType = selectedMovieSortType,
                    selectedShowSortType = selectedShowSortType,
                    selectedMediaType = selectedMediaType,
                    displaySortScreen = displaySortScreen
                )
            }
            currentDestination?.hasRoute<WatchlistRoute>() == true -> {
                WatchlistSortBottomSheet(
                    mainViewModel = mainViewModel,
                    displaySortScreen = displaySortScreen
                )
            }
        }
    }
}
