package common.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSizeInfo(): ScreenSizeInfo {
    val density = LocalDensity.current
    val config = LocalWindowInfo.current.containerSize

    return ScreenSizeInfo(
        widthPx = config.width,
        heightPx = config.height,
        widthDp = with(density) { config.width.toDp() },
        heightDp = with(density) { config.height.toDp() },
    )
}
