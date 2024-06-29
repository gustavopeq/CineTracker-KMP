package common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    errorImageRes: Int? = null,
    alpha: Float = DefaultAlpha,
) {
    var loadingSuccess by remember { mutableStateOf(false) }
    var onError by remember { mutableStateOf(false) }
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

//        GlideImage(
//            modifier = modifier.size(widthDp, heightDp),
//            model = imageUrl,
//            contentDescription = null,
//            contentScale = contentScale,
//            alpha = alpha,
//        ) {
//            it.listener(
//                object : RequestListener<Drawable> {
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        model: Any,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource,
//                        isFirstResource: Boolean,
//                    ): Boolean {
//                        loadingSuccess = true
//                        return false
//                    }
//
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>,
//                        isFirstResource: Boolean,
//                    ): Boolean {
//                        onError = true
//                        return false
//                    }
//                },
//            )
//        }
//        if (!loadingSuccess && onError) {
//            Image(
//                painter = painterResource(
//                    id = errorImageRes ?: R.drawable.missing_profile_pic,
//                ),
//                contentDescription = null,
//                modifier = modifier.size(widthDp, heightDp),
//                contentScale = contentScale,
//            )
//        }
//        if (!loadingSuccess) {
//            ComponentPlaceholder(
//                modifier = modifier.width(widthDp).height(heightDp),
//            )
//        }
    }
}
