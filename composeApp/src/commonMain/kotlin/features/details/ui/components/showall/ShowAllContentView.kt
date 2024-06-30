package features.details.ui.components.showall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.back_arrow_description
import cinetracker_kmp.composeapp.generated.resources.ic_back_arrow
import cinetracker_kmp.composeapp.generated.resources.see_all_movies_label
import cinetracker_kmp.composeapp.generated.resources.see_all_shows_label
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.domain.util.UiConstants.BROWSE_CARD_PADDING_HORIZONTAL
import common.domain.util.UiConstants.BROWSE_CARD_PADDING_VERTICAL
import common.domain.util.UiConstants.BROWSE_MIN_CARD_WIDTH
import common.domain.util.UiConstants.DEFAULT_MARGIN
import common.domain.util.UiConstants.FOREGROUND_INDEX
import common.domain.util.UiConstants.MAX_COUNT_PERSON_ADDITIONAL_CONTENT
import common.domain.util.UiConstants.RETURN_TOP_BAR_HEIGHT
import common.domain.util.UiConstants.SMALL_PADDING
import common.domain.util.calculateCardsPerRow
import common.domain.util.dpToPx
import common.domain.util.pxToDp
import common.ui.components.card.DefaultContentCard
import common.util.getScreenSizeInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowAllContentList(
    showAllMediaType: MediaType,
    contentList: List<GenericContent>,
    goToDetails: (Int, MediaType) -> Unit,
    onBackBtnPress: () -> Unit,
) {
    val filteredItems = contentList.filter { it.mediaType == showAllMediaType }

//    BackHandler {
//        onBackBtnPress()
//    }

    Column {
        TopBar(
            mediaType = showAllMediaType,
            onBackBtnPress = onBackBtnPress,
        )
        AllContentGrid(
            itemList = filteredItems,
            goToDetails = goToDetails,
        )
    }
}

@Composable
private fun AllContentGrid(
    itemList: List<GenericContent>,
    goToDetails: (Int, MediaType) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    val density = LocalDensity.current
    val screenWidth = density.run { getScreenSizeInfo().widthPx }
    val spacing = density.run { DEFAULT_MARGIN.dp.roundToPx() }
    val minCardSize = pxToDp(BROWSE_MIN_CARD_WIDTH, density)

    val (numCardsPerRow, adjustedCardSize) = calculateCardsPerRow(
        screenWidth,
        dpToPx(minCardSize, density),
        spacing,
        density,
    )

    LaunchedEffect(Unit) {
        lazyGridState.scrollToItem(MAX_COUNT_PERSON_ADDITIONAL_CONTENT - 2)
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SMALL_PADDING.dp),
        state = lazyGridState,
        columns = GridCells.Fixed(numCardsPerRow),
    ) {
        items(itemList) { content ->
            DefaultContentCard(
                modifier = Modifier
                    .width(adjustedCardSize)
                    .padding(
                        horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
                        vertical = BROWSE_CARD_PADDING_VERTICAL.dp,
                    ),
                cardWidth = adjustedCardSize,
                imageUrl = content.posterPath,
                title = content.name,
                rating = content.rating,
                goToDetails = {
                    goToDetails(content.id, content.mediaType)
                },
            )
        }
    }
}

@Composable
private fun TopBar(
    mediaType: MediaType,
    onBackBtnPress: () -> Unit,
) {
    val title = if (mediaType == MediaType.SHOW) {
        stringResource(resource = Res.string.see_all_shows_label)
    } else {
        stringResource(resource = Res.string.see_all_movies_label)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RETURN_TOP_BAR_HEIGHT.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
            )
            .zIndex(FOREGROUND_INDEX),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { onBackBtnPress() },
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_back_arrow),
                contentDescription = stringResource(resource = Res.string.back_arrow_description),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}
