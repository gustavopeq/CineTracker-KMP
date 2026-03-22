package common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_outlined_star
import cinetracker_kmp.composeapp.generated.resources.ic_star
import common.ui.theme.PrimaryBlueColor
import common.util.UiConstants.RATING_STAR_DEFAULT_SIZE
import common.util.formatRating
import org.jetbrains.compose.resources.painterResource

@Composable
fun RatingComponent(
    modifier: Modifier = Modifier,
    rating: Double?,
    ratingIconSize: Int? = RATING_STAR_DEFAULT_SIZE,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.offset(x = (-0.5).dp)
    ) {
        Image(
            modifier = Modifier.size((ratingIconSize ?: RATING_STAR_DEFAULT_SIZE).dp),
            painter = painterResource(resource = Res.drawable.ic_star),
            contentDescription = null
        )
        Text(
            text = rating.formatRating(),
            color = MaterialTheme.colorScheme.onPrimary,
            style = textStyle
        )
    }
}

@Composable
fun PersonalRatingComponent(
    modifier: Modifier = Modifier,
    rating: String,
    ratingIconSize: Int? = RATING_STAR_DEFAULT_SIZE,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    starColor: Color = PrimaryBlueColor,
    onRatingClick: (() -> Unit)? = null
) {
    val isRatingSet = rating.toDoubleOrNull() != null
    val starIcon = if (isRatingSet) Res.drawable.ic_star else Res.drawable.ic_outlined_star

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .offset(x = (-0.5).dp)
            .then(
                if (onRatingClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(
                                bounded = true,
                                color = starColor
                            ),
                            onClick = onRatingClick
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                } else {
                    Modifier
                }
            )
    ) {
        Image(
            modifier = Modifier.size((ratingIconSize ?: RATING_STAR_DEFAULT_SIZE).dp),
            painter = painterResource(resource = starIcon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(starColor)
        )
        Text(
            text = rating,
            color = MaterialTheme.colorScheme.onPrimary,
            style = textStyle
        )
    }
}
