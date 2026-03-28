package common.util.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun SetStatusBarColor(newColor: Color) {
    // Handled by enableEdgeToEdge in MainActivity + SystemBarsContainer background
}

@Composable
actual fun SetNavigationBarColor(newColor: Color) {
    // Handled by enableEdgeToEdge in MainActivity + Compose background content
}
