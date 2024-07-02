package common.ui.components.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import common.ui.MainViewModel
import features.browse.BrowseScreen
import features.browse.ui.components.BrowseSortBottomSheet
import features.watchlist.WatchlistScreen
import features.watchlist.ui.components.WatchlistSortBottomSheet

@Composable
fun ModalComponents(
    mainViewModel: MainViewModel,
    showSortBottomSheet: Boolean,
    displaySortScreen: (Boolean) -> Unit,
) {
    val selectedMovieSortType by mainViewModel.movieSortType.collectAsState()
    val selectedShowSortType by mainViewModel.showSortType.collectAsState()
    val selectedMediaType by mainViewModel.currentMediaTypeSelected.collectAsState()
    val currentScreen by mainViewModel.currentScreen.collectAsState()
    val selectedWatchlistSortType by mainViewModel.watchlistSort.collectAsState()

    if (showSortBottomSheet) {
        when (currentScreen) {
            BrowseScreen.route() -> {
                BrowseSortBottomSheet(
                    mainViewModel = mainViewModel,
                    selectedMovieSortType = selectedMovieSortType,
                    selectedShowSortType = selectedShowSortType,
                    selectedMediaType = selectedMediaType,
                    displaySortScreen = displaySortScreen,
                )
            }
            WatchlistScreen.route() -> {
                WatchlistSortBottomSheet(
                    mainViewModel = mainViewModel,
                    selectedWatchlistSortType = selectedWatchlistSortType,
                    displaySortScreen = displaySortScreen,
                )
            }
        }
    }
}
