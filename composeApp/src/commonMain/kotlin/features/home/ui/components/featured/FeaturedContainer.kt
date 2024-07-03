package features.home.ui.components.featured

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.see_details_button_text
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.domain.util.UiConstants.BACKGROUND_INDEX
import common.domain.util.UiConstants.DEFAULT_MARGIN
import common.domain.util.UiConstants.DEFAULT_PADDING
import common.domain.util.UiConstants.HOME_BACKGROUND_ALPHA
import common.domain.util.UiConstants.POSTER_ASPECT_RATIO
import common.ui.components.GradientDirections
import common.ui.components.NetworkImage
import common.ui.components.button.GenericButton
import common.ui.components.classicVerticalGradientBrush
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeaturedInfo(
    featuredContent: GenericContent?,
    goToDetails: (Int, MediaType) -> Unit,
) {
    featuredContent?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .classicVerticalGradientBrush(
                    direction = GradientDirections.UP,
                ),
        ) {
            Column(
                modifier = Modifier.padding(DEFAULT_MARGIN.dp),
            ) {
                Text(
                    text = featuredContent.name.orEmpty(),
                    style = MaterialTheme.typography.displayLarge,
                )
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                Text(
                    text = featuredContent.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                GenericButton(
                    buttonText = stringResource(resource = Res.string.see_details_button_text),
                    onClick = {
                        goToDetails(featuredContent.id, featuredContent.mediaType)
                    },
                )
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
            }
        }
    }
}

@Composable
fun FeaturedBackgroundImage(
    imageUrl: String,
    posterHeight: Float,
    showBackgroundImage: Boolean,
) {
    Box {
        NetworkImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(posterHeight.dp)
                .zIndex(BACKGROUND_INDEX)
                .aspectRatio(POSTER_ASPECT_RATIO),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    if (showBackgroundImage) {
                        MaterialTheme.colorScheme.primary.copy(HOME_BACKGROUND_ALPHA)
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                ),
        )
    }
}
