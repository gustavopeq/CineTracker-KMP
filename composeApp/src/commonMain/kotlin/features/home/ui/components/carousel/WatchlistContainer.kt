package features.home.ui.components.carousel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.carousel_see_all_button
import cinetracker_kmp.composeapp.generated.resources.empty_list_header
import cinetracker_kmp.composeapp.generated.resources.home_my_watchlist_header
import cinetracker_kmp.composeapp.generated.resources.ic_chevron_right
import cinetracker_kmp.composeapp.generated.resources.watchlist_carousel_empty_message
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.ui.components.card.ImageContentCard
import common.util.UiConstants.CAROUSEL_CARDS_WIDTH
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.LARGE_PADDING
import common.util.UiConstants.SMALL_PADDING
import common.util.UiConstants.WATCHLIST_CAROUSEL_MAX_COUNT
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WatchlistCarousel(
    watchlist: List<GenericContent>,
    currentScreenWidth: Float,
    goToDetails: (Int, MediaType) -> Unit,
    goToWatchlist: () -> Unit,
) {
    val carouselHeader = Res.string.home_my_watchlist_header

    if (watchlist.isNotEmpty()) {
        ClassicCarousel(
            carouselHeaderRes = carouselHeader,
            itemList = watchlist.take(WATCHLIST_CAROUSEL_MAX_COUNT),
            currentScreenWidth = currentScreenWidth,
            goToDetails = goToDetails,
            headerAdditionalAction = {
                CarouselSeeAllOption(goToWatchlist)
            },
        ) { item, goToDetails ->
            ImageContentCard(
                modifier = Modifier.padding(
                    top = DEFAULT_PADDING.dp,
                    bottom = DEFAULT_PADDING.dp,
                    end = DEFAULT_PADDING.dp,
                ),
                item = item,
                adjustedCardSize = CAROUSEL_CARDS_WIDTH.dp,
                goToDetails = goToDetails,
            )
        }
    } else {
        WatchlistEmptyCarousel(
            carouselHeaderRes = carouselHeader,
            headerAdditionalAction = {
                CarouselSeeAllOption(goToWatchlist)
            },
        )
    }

    Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
}

@Composable
private fun WatchlistEmptyCarousel(
    carouselHeaderRes: StringResource,
    headerAdditionalAction: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(DEFAULT_MARGIN.dp)
            .fillMaxWidth(),
    ) {
        CarouselHeaderRow(
            carouselHeaderRes = carouselHeaderRes,
            headerAdditionalAction = headerAdditionalAction,
        )
        Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(resource = Res.string.empty_list_header),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
            Text(
                text = stringResource(resource = Res.string.watchlist_carousel_empty_message),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.surface,
            )
        }
    }
}

@Composable
private fun CarouselSeeAllOption(goToWatchlist: () -> Unit) {
    Row(
        modifier = Modifier.clickable(
            onClick = goToWatchlist,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(resource = Res.string.carousel_see_all_button),
            style = MaterialTheme.typography.titleSmall,
        )
        Icon(
            painter = painterResource(resource = Res.drawable.ic_chevron_right),
            contentDescription = null,
        )
    }
}
