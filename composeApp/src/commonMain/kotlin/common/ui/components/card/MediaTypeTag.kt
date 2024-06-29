package common.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.movie_tag
import cinetracker_kmp.composeapp.generated.resources.show_tag
import common.domain.models.util.MediaType
import common.ui.theme.PrimaryYellowColor_90
import org.jetbrains.compose.resources.stringResource

@Composable
fun MediaTypeTag(
    modifier: Modifier = Modifier,
    mediaType: MediaType,
) {
    val mediaTypeTag = if (mediaType == MediaType.MOVIE) {
        stringResource(resource = Res.string.movie_tag)
    } else {
        stringResource(resource = Res.string.show_tag)
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 50.dp)
            .background(color = PrimaryYellowColor_90),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = mediaTypeTag,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}
