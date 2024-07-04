package features.home.ui.components.featured

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.ui.components.NetworkImage
import common.ui.components.RatingComponent
import common.ui.components.card.MediaTypeTag
import common.ui.theme.MainBarGreyColor
import common.util.Constants.BASE_ORIGINAL_IMAGE_URL
import common.util.UiConstants
import common.util.UiConstants.BACKDROP_ASPECT_RATIO
import common.util.UiConstants.BROWSE_CARD_DEFAULT_ELEVATION
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING

@Composable
fun SecondaryFeaturedInfo(
    featuredItem: GenericContent?,
    goToDetails: (Int, MediaType) -> Unit,
) {
    featuredItem?.let {
        val fullImageUrl = BASE_ORIGINAL_IMAGE_URL + featuredItem.backdropPath

        Column(
            modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp),
        ) {
            Card(
                modifier = Modifier.clickable(
                    onClick = { goToDetails(featuredItem.id, featuredItem.mediaType) },
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MainBarGreyColor,
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = BROWSE_CARD_DEFAULT_ELEVATION.dp,
                ),
            ) {
                Box {
                    NetworkImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(BACKDROP_ASPECT_RATIO),
                        imageUrl = fullImageUrl,
                    )
                    MediaTypeTag(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(bottomStart = 4.dp)),
                        mediaType = featuredItem.mediaType,
                    )
                }

                Column(
                    modifier = Modifier.padding(DEFAULT_PADDING.dp),
                ) {
                    HomeCardTitle(
                        title = featuredItem.name,
                    )
                    if (featuredItem.rating > 0.0) {
                        Spacer(modifier = Modifier.height(UiConstants.SMALL_PADDING.dp))
                        RatingComponent(rating = featuredItem.rating)
                    }
                    Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                    Text(
                        text = featuredItem.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))
    }
}
