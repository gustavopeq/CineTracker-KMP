package common.util.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor

@Composable
expect fun SetStatusBarColor(newColor: Color = MainBarGreyColor)

@Composable
expect fun SetNavigationBarColor(newColor: Color = PrimaryBlackColor)
