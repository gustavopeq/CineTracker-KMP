package common.ui.components.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import common.ui.components.NetworkImage
import common.ui.theme.MainBarGreyColor
import common.ui.theme.RoundCornerShapes
import common.util.Constants.BASE_500_IMAGE_URL
import common.util.UiConstants.BROWSE_CARD_DEFAULT_ELEVATION
import common.util.UiConstants.BROWSE_CARD_PADDING_HORIZONTAL
import common.util.UiConstants.BROWSE_CARD_PADDING_VERTICAL
import common.util.UiConstants.POSTER_ASPECT_RATIO_MULTIPLY

@Composable
fun PersonImages(
    modifier: Modifier = Modifier,
    cardWidth: Dp,
    imageUrl: String?,
) {
    val fullImageUrl = BASE_500_IMAGE_URL + imageUrl
    val imageHeight = cardWidth * POSTER_ASPECT_RATIO_MULTIPLY

    Card(
        modifier = modifier.padding(
            horizontal = BROWSE_CARD_PADDING_HORIZONTAL.dp,
            vertical = BROWSE_CARD_PADDING_VERTICAL.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MainBarGreyColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = BROWSE_CARD_DEFAULT_ELEVATION.dp,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            NetworkImage(
                imageUrl = fullImageUrl,
                modifier = Modifier.clip(RoundCornerShapes.medium),
                widthDp = cardWidth,
                heightDp = imageHeight,
            )
        }
    }
}
