package common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

data class ScreenSizeInfo(
    val widthPx: Int,
    val heightPx: Int,
    val widthDp: Dp,
    val heightDp: Dp,
)

@Composable
expect fun getScreenSizeInfo(): ScreenSizeInfo
