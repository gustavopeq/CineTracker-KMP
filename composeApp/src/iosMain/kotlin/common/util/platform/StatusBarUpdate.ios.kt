package common.util.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun SetStatusBarColor(newColor: Color) {
    // TODO status bar color update on IOS
}

@Composable
actual fun SetNavigationBarColor(newColor: Color) {
    // iOS home indicator area color is controlled by the Swift ContentView background
}
