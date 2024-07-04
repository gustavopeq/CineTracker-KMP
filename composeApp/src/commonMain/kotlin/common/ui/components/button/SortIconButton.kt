package common.ui.components.button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_sort
import common.ui.MainViewModel
import common.util.UiConstants.BROWSE_SORT_ICON_SIZE
import features.watchlist.WatchlistScreen
import org.jetbrains.compose.resources.painterResource

@Composable
fun SortIconButton(
    mainViewModel: MainViewModel,
    currentScreen: String,
    displaySortScreen: (Boolean) -> Unit,
) {
    val watchlistSortSelected by mainViewModel.watchlistSort.collectAsState()
    val iconColor = if (currentScreen == WatchlistScreen.route() && watchlistSortSelected != null) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    IconButton(
        onClick = { displaySortScreen(true) },
    ) {
        Icon(
            modifier = Modifier.size(BROWSE_SORT_ICON_SIZE.dp),
            painter = painterResource(resource = Res.drawable.ic_sort),
            tint = iconColor,
            contentDescription = null,
        )
    }
}
