package common.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenSizeInfo(): ScreenSizeInfo {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val hDp = config.screenHeightDp.dp
    val wDp = config.screenWidthDp.dp

    return ScreenSizeInfo(
        widthPx = with(density) { wDp.roundToPx() },
        heightPx = with(density) { hDp.roundToPx() },
        widthDp = wDp,
        heightDp = hDp,
    )
}
