package common.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import common.ui.theme.PrimaryBlackColor

@Composable
actual fun SetStatusBarColor(newColor: Color) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    DisposableEffect(Unit) {
        window?.statusBarColor = newColor.toArgb()

        onDispose {
            window?.statusBarColor = PrimaryBlackColor.toArgb()
        }
    }
}
