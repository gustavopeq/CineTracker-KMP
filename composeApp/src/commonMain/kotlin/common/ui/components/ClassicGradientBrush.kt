package common.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.classicVerticalGradientBrush(
    colorList: List<Color> = listOf(Color.Black, Color.Transparent),
    direction: GradientDirections = GradientDirections.DOWN,
): Modifier {
    return when (direction) {
        GradientDirections.UP -> this.background(
            Brush.verticalGradient(
                colors = colorList,
                startY = Float.POSITIVE_INFINITY,
                endY = 0f,
            ),
        )
        GradientDirections.DOWN -> this.background(
            Brush.verticalGradient(
                colors = colorList,
                startY = 0f,
                endY = Float.POSITIVE_INFINITY,
            ),
        )
        GradientDirections.LEFT -> this.background(
            Brush.horizontalGradient(
                colors = colorList,
                startX = Float.POSITIVE_INFINITY,
                endX = 0f,
            ),
        )
        GradientDirections.RIGHT -> this.background(
            Brush.verticalGradient(
                colors = colorList,
                startY = 0f,
                endY = Float.POSITIVE_INFINITY,
            ),
        )
    }
}

enum class GradientDirections {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}
