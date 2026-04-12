package features.auth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryYellowColor

private const val GRADIENT_RADIUS = 1200f
private const val GRADIENT_YELLOW_ALPHA = 0.1f

@Composable
fun AuthBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PrimaryBlackColor,
                        PrimaryYellowColor.copy(alpha = GRADIENT_YELLOW_ALPHA)
                    ),
                    center = Offset.Unspecified,
                    radius = GRADIENT_RADIUS
                )
            ),
        content = content
    )
}
