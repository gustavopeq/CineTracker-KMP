package common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import common.ui.theme.MainBarGreyColor

@Composable
expect fun SetStatusBarColor(newColor: Color = MainBarGreyColor)
