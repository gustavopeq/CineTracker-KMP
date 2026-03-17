package features.watchlist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.all_tag
import cinetracker_kmp.composeapp.generated.resources.filter_by_header
import cinetracker_kmp.composeapp.generated.resources.movie_only_tag
import cinetracker_kmp.composeapp.generated.resources.show_only_tag
import cinetracker_kmp.composeapp.generated.resources.sort_by_header
import cinetracker_kmp.composeapp.generated.resources.watchlist_sort_options_header
import common.domain.models.util.MediaType
import common.ui.MainViewModel
import common.ui.components.bottomsheet.GenericBottomSheet
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.SMALL_MARGIN
import common.util.UiConstants.SMALL_PADDING
import features.watchlist.ui.model.WatchlistRatingSort
import org.jetbrains.compose.resources.stringResource

@Composable
fun WatchlistSortBottomSheet(
    mainViewModel: MainViewModel,
    displaySortScreen: (Boolean) -> Unit,
) {
    val watchlistSort by mainViewModel.watchlistSort.collectAsState()

    val dismissBottomSheet: () -> Unit = {
        displaySortScreen(false)
    }

    GenericBottomSheet(
        dismissBottomSheet = dismissBottomSheet,
        headerText = stringResource(resource = Res.string.watchlist_sort_options_header),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DEFAULT_PADDING.dp)
        ) {
            Spacer(modifier = Modifier.height(SMALL_MARGIN.dp))

            // Filter Section
            SectionHeader(text = stringResource(resource = Res.string.filter_by_header))
            
            WatchlistOptionRow(
                text = stringResource(resource = Res.string.all_tag),
                isSelected = watchlistSort.mediaType == null,
                onClick = { mainViewModel.updateWatchlistSort(null) }
            )
            WatchlistOptionRow(
                text = stringResource(resource = Res.string.movie_only_tag),
                isSelected = watchlistSort.mediaType == MediaType.MOVIE,
                onClick = { mainViewModel.updateWatchlistSort(MediaType.MOVIE) }
            )
            WatchlistOptionRow(
                text = stringResource(resource = Res.string.show_only_tag),
                isSelected = watchlistSort.mediaType == MediaType.SHOW,
                onClick = { mainViewModel.updateWatchlistSort(MediaType.SHOW) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = DEFAULT_PADDING.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )

            // Sort Section
            SectionHeader(text = stringResource(resource = Res.string.sort_by_header))

            WatchlistOptionRow(
                text = stringResource(resource = WatchlistRatingSort.PublicRating.titleRes),
                isSelected = watchlistSort.ratingSort == WatchlistRatingSort.PublicRating,
                onClick = {
                    val newSort = if (watchlistSort.ratingSort == WatchlistRatingSort.PublicRating) {
                        null
                    } else {
                        WatchlistRatingSort.PublicRating
                    }
                    mainViewModel.updateWatchlistRatingSort(newSort)
                }
            )
            WatchlistOptionRow(
                text = stringResource(resource = WatchlistRatingSort.PersonalRating.titleRes),
                isSelected = watchlistSort.ratingSort == WatchlistRatingSort.PersonalRating,
                onClick = {
                    val newSort = if (watchlistSort.ratingSort == WatchlistRatingSort.PersonalRating) {
                        null
                    } else {
                        WatchlistRatingSort.PersonalRating
                    }
                    mainViewModel.updateWatchlistRatingSort(newSort)
                }
            )
            
            Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(bottom = SMALL_PADDING.dp)
    )
}

@Composable
private fun WatchlistOptionRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            modifier = Modifier.size(40.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.secondary,
                unselectedColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            ),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = SMALL_PADDING.dp),
        )
    }
}
