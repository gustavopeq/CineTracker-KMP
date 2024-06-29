package common.ui.components.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import common.domain.util.Constants.BASE_500_IMAGE_URL
import common.domain.util.UiConstants.BROWSE_CARD_DEFAULT_ELEVATION
import common.domain.util.UiConstants.POSTER_ASPECT_RATIO_MULTIPLY
import common.domain.util.UiConstants.SMALL_PADDING
import common.ui.components.NetworkImage
import common.ui.components.RatingComponent
import common.ui.theme.MainBarGreyColor
import common.ui.theme.RoundCornerShapes

@Composable
fun DefaultContentCard(
    modifier: Modifier = Modifier,
    cardWidth: Dp,
    imageUrl: String?,
    title: String?,
    rating: Double?,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    ratingIconSize: Int? = null,
    goToDetails: () -> Unit,
) {
    val fullImageUrl = BASE_500_IMAGE_URL + imageUrl
    val imageHeight = cardWidth * POSTER_ASPECT_RATIO_MULTIPLY

    Card(
        modifier = modifier
            .width(cardWidth),
        onClick = goToDetails,
        colors = CardDefaults.cardColors(
            containerColor = MainBarGreyColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = BROWSE_CARD_DEFAULT_ELEVATION.dp,
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = SMALL_PADDING.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            NetworkImage(
                imageUrl = fullImageUrl,
                modifier = Modifier.clip(RoundCornerShapes.small),
                widthDp = cardWidth,
                heightDp = imageHeight,
            )
            Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
            Text(
                text = title.orEmpty(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            RatingComponent(
                rating = rating,
                textStyle = textStyle,
                ratingIconSize = ratingIconSize,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
