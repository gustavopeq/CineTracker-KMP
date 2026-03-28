package features.home.ui.components.featured

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.home_in_my_list
import cinetracker_kmp.composeapp.generated.resources.home_my_list
import cinetracker_kmp.composeapp.generated.resources.ic_check
import cinetracker_kmp.composeapp.generated.resources.ic_watchlist_add_list
import cinetracker_kmp.composeapp.generated.resources.see_details_button_text
import common.domain.models.content.GenericContent
import common.domain.models.util.MediaType
import common.ui.components.GradientDirections
import common.ui.components.NetworkImage
import common.ui.components.button.GenericButton
import common.ui.components.classicVerticalGradientBrush
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.BACKGROUND_INDEX
import common.util.UiConstants.CLASSIC_BUTTON_BORDER_SIZE
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.HOME_BACKGROUND_ALPHA
import common.util.UiConstants.POSTER_ASPECT_RATIO
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val MY_LIST_ICON_SIZE = 24

@Composable
fun FeaturedInfo(
    featuredContent: GenericContent?,
    isInAnyList: Boolean,
    goToDetails: (Int, MediaType) -> Unit,
    onMyListClick: () -> Unit
) {
    featuredContent?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .classicVerticalGradientBrush(
                    direction = GradientDirections.UP
                )
        ) {
            Column(
                modifier = Modifier.padding(DEFAULT_MARGIN.dp)
            ) {
                Text(
                    text = featuredContent.name.orEmpty(),
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                Text(
                    text = featuredContent.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    GenericButton(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        buttonText = stringResource(resource = Res.string.see_details_button_text),
                        onClick = {
                            goToDetails(featuredContent.id, featuredContent.mediaType)
                        }
                    )
                    Spacer(modifier = Modifier.width(DEFAULT_PADDING.dp))
                    MyListButton(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        isInAnyList = isInAnyList,
                        onClick = onMyListClick
                    )
                }
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
            }
        }
    }
}

@Composable
private fun MyListButton(modifier: Modifier = Modifier, isInAnyList: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(CLASSIC_BUTTON_BORDER_SIZE.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreyColor.copy(alpha = 0.3f))
    ) {
        Icon(
            painter = painterResource(
                if (isInAnyList) Res.drawable.ic_check else Res.drawable.ic_watchlist_add_list
            ),
            contentDescription = null,
            tint = PrimaryWhiteColor,
            modifier = Modifier.size(MY_LIST_ICON_SIZE.dp)
        )
        Spacer(modifier = Modifier.width(DEFAULT_PADDING.dp))
        Text(
            text = stringResource(
                if (isInAnyList) Res.string.home_in_my_list else Res.string.home_my_list
            ),
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryWhiteColor
        )
    }
}

@Composable
fun FeaturedBackgroundImage(imageUrl: String, posterHeight: Float, showBackgroundImage: Boolean) {
    Box {
        NetworkImage(
            imageUrl = imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(posterHeight.dp)
                .zIndex(BACKGROUND_INDEX)
                .aspectRatio(POSTER_ASPECT_RATIO)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    if (showBackgroundImage) {
                        MaterialTheme.colorScheme.primary.copy(HOME_BACKGROUND_ALPHA)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
        )
    }
}
