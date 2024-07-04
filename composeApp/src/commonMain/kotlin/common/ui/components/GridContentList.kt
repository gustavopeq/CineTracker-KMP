package common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.see_all_button_label
import common.domain.models.content.GenericContent
import common.domain.models.person.PersonImage
import common.domain.models.util.MediaType
import common.ui.components.button.GenericButton
import common.ui.components.card.DefaultContentCard
import common.ui.components.card.PersonImages
import common.util.UiConstants.BROWSE_CARD_PADDING_HORIZONTAL
import common.util.UiConstants.BROWSE_CARD_PADDING_VERTICAL
import common.util.UiConstants.BROWSE_MIN_CARD_WIDTH
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.calculateCardsPerRow
import common.util.dpToPx
import common.util.platform.getScreenSizeInfo
import common.util.pxToDp
import common.util.removeParentPadding
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> GenericGrid(
    itemList: List<T>,
    maxCardsNumber: Int,
    displayItem: @Composable (T, Dp) -> Unit,
) {
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

    Column(
        modifier = Modifier.fillMaxWidth().removeParentPadding(DEFAULT_MARGIN.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val filteredList = itemList.take(maxCardsNumber)
            .chunked(numCardsPerRow)

        filteredList.forEach { rowItems ->
            Row {
                rowItems.forEach { content ->
                    displayItem(content, adjustedCardSize)
                }
            }
        }
    }
}

@Composable
fun GridContentList(
    mediaContentList: List<GenericContent>,
    maxCardsNumber: Int? = null,
    showSeeAllButton: Boolean = false,
    openContentDetails: (Int, MediaType) -> Unit,
    onSeeAll: () -> Unit = {},
) {
    Column {
        GenericGrid(
            itemList = mediaContentList,
            maxCardsNumber = maxCardsNumber ?: mediaContentList.size,
        ) { content, size ->
            DefaultContentCard(
                modifier = Modifier
                    .width(size)
                    .padding(
                        horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
                        vertical = BROWSE_CARD_PADDING_VERTICAL.dp,
                    ),
                cardWidth = size,
                imageUrl = content.posterPath,
                title = content.name,
                rating = content.rating,
                goToDetails = {
                    openContentDetails(content.id, content.mediaType)
                },
            )
        }
        if (showSeeAllButton && maxCardsNumber != null && mediaContentList.size > maxCardsNumber) {
            GenericButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = DEFAULT_MARGIN.dp),
                buttonText = stringResource(resource = Res.string.see_all_button_label),
                onClick = onSeeAll,
            )
        }
    }
}

@Composable
fun GridImageList(
    personImageList: List<PersonImage>,
    maxCardsNumber: Int? = null,
) {
    GenericGrid(
        itemList = personImageList,
        maxCardsNumber = maxCardsNumber ?: personImageList.size,
    ) { personImage, size ->
        PersonImages(
            modifier = Modifier.width(size),
            cardWidth = size,
            imageUrl = personImage.filePath,
        )
    }
}
