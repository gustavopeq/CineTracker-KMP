package common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.SubcomposeAsyncImage

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    widthDp: Dp = Dp.Unspecified,
    heightDp: Dp = Dp.Unspecified,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
) {
    Box {
        SubcomposeAsyncImage(
            modifier = modifier.size(widthDp, heightDp),
            model = imageUrl,
            contentDescription = null,
            contentScale = contentScale,
            alpha = alpha,
            loading = {
                ComponentPlaceholder(
                    modifier = modifier.width(widthDp).height(heightDp),
                )
            },
        )
    }
}
