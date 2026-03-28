package features.onboarding.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import common.ui.theme.PrimaryYellowColor
import common.util.UiConstants.DEFAULT_PADDING

private const val INDICATOR_HEIGHT = 6
private const val ACTIVE_INDICATOR_WIDTH = 32
private const val INACTIVE_INDICATOR_SIZE = 6
private const val INACTIVE_COLOR = 0xFF262626
private const val ANIMATION_DURATION_MS = 300

@Composable
fun OnboardingIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DEFAULT_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isActive) ACTIVE_INDICATOR_WIDTH.dp else INACTIVE_INDICATOR_SIZE.dp,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_MS)
            )
            val color by animateColorAsState(
                targetValue = if (isActive) {
                    PrimaryYellowColor
                } else {
                    androidx.compose.ui.graphics.Color(INACTIVE_COLOR)
                },
                animationSpec = tween(durationMillis = ANIMATION_DURATION_MS)
            )

            Box(
                modifier = Modifier
                    .width(width)
                    .height(INDICATOR_HEIGHT.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(color)
            )
        }
    }
}
