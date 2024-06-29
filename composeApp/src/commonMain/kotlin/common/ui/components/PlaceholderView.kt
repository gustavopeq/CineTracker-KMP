package common.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import common.ui.theme.placeholderGrey
import common.ui.theme.placeholderGrey2

@Composable
fun ComponentPlaceholder(
    modifier: Modifier = Modifier,
    initialValue: Float = -1000F,
    targetValue: Float = 2000F,
    durationMillis: Int = 3000,
    shimmerColorShades: List<Color> = listOf(
        placeholderGrey,
        placeholderGrey,
        placeholderGrey,
        placeholderGrey2,
        placeholderGrey2,
    ),
) {
    val transition = rememberInfiniteTransition(label = "transition")
    val translateAnim by transition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "placeholderAnimation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColorShades,
        start = Offset.Zero,
        end = Offset(translateAnim, translateAnim),
    )

    Spacer(
        modifier = modifier
            .background(brush),
    )
}
