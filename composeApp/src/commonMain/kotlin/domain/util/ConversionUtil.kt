package domain.util

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

fun pxToDp(
    pixels: Int,
    density: Density,
): Dp = with(density) { pixels.toDp() }

fun dpToPx(
    dp: Dp,
    density: Density,
): Int = with(density) { dp.roundToPx() }
