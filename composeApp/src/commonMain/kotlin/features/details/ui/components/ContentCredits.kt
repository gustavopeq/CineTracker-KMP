package features.details.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.movie_details_cast_label
import common.domain.models.content.ContentCast
import common.domain.models.util.MediaType
import common.domain.util.Constants.BASE_500_IMAGE_URL
import common.domain.util.UiConstants.DEFAULT_MARGIN
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.domain.util.UiConstants.DETAILS_CAST_CARD_HEIGHT
import common.domain.util.UiConstants.DETAILS_CAST_PICTURE_SIZE
import common.domain.util.UiConstants.SMALL_PADDING
import common.domain.util.removeParentPadding
import common.ui.components.NetworkImage
import common.util.getScreenSizeInfo
import org.jetbrains.compose.resources.stringResource

@Composable
fun CastCarousel(
    contentCredits: List<ContentCast>,
    goToDetails: (Int, MediaType) -> Unit,
) {
    DetailDescriptionLabel(
        labelText = stringResource(resource = Res.string.movie_details_cast_label),
        textStyle = MaterialTheme.typography.displayMedium,
    )

    Spacer(modifier = Modifier.height(SMALL_PADDING.dp))

    val currentScreenWidth = getScreenSizeInfo().widthDp.value
    val cardWidth = DETAILS_CAST_PICTURE_SIZE.dp + DEFAULT_PADDING.dp
    val cardsCountInScreen = currentScreenWidth / cardWidth.value

    LazyRow(
        modifier = Modifier.removeParentPadding(DEFAULT_MARGIN.dp),
    ) {
        if (contentCredits.size >= cardsCountInScreen) {
            item {
                Spacer(modifier = Modifier.width(DEFAULT_MARGIN.dp))
            }
        }
        items(contentCredits) { cast ->
            val castImageUrl = BASE_500_IMAGE_URL + cast.profilePoster

            Column(
                modifier = Modifier
                    .width(DETAILS_CAST_PICTURE_SIZE.dp + DEFAULT_PADDING.dp)
                    .height(DETAILS_CAST_CARD_HEIGHT.dp)
                    .padding(horizontal = DEFAULT_PADDING.dp)
                    .clickable {
                        goToDetails(cast.id, MediaType.PERSON)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NetworkImage(
                    imageUrl = castImageUrl,
                    modifier = Modifier
                        .size(DETAILS_CAST_PICTURE_SIZE.dp)
                        .clip(CircleShape),
                )
                Text(
                    text = cast.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
                Text(
                    text = cast.character,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
