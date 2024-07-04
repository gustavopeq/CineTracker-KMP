package common.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_star
import common.util.UiConstants.RATING_STAR_DEFAULT_SIZE
import common.util.formatRating
import org.jetbrains.compose.resources.painterResource

@Composable
fun RatingComponent(
    modifier: Modifier = Modifier,
    rating: Double?,
    ratingIconSize: Int? = RATING_STAR_DEFAULT_SIZE,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.offset(x = (-0.5).dp),
    ) {
        Image(
            modifier = Modifier.size((ratingIconSize ?: RATING_STAR_DEFAULT_SIZE).dp),
            painter = painterResource(resource = Res.drawable.ic_star),
            contentDescription = null,
        )
        Text(
            text = rating.formatRating(),
            color = MaterialTheme.colorScheme.onPrimary,
            style = textStyle,
        )
    }
}
