package common.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import common.domain.models.util.MediaType

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

fun sharedPosterKey(tag: String, contentId: Int, mediaType: MediaType): String =
    "poster_${tag}_${contentId}_${mediaType.name}"

@Composable
fun rememberSharedElementModifier(key: String?): Modifier {
    if (key == null) return Modifier
    val scope = LocalSharedTransitionScope.current ?: return Modifier
    val visibilityScope = LocalAnimatedVisibilityScope.current ?: return Modifier
    return with(scope) {
        Modifier.sharedElement(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = visibilityScope
        )
    }
}
