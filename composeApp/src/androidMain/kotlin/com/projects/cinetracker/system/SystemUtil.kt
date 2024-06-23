package com.projects.cinetracker.system

import android.app.Activity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import common.domain.util.UiConstants.SYSTEM_BOTTOM_NAV_PADDING
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor

@Composable
fun SystemNavBarSpacer() {
    Spacer(modifier = Modifier.height(SYSTEM_BOTTOM_NAV_PADDING.dp))
}

@Composable
fun SetStatusBarColor(
    color: Color = MainBarGreyColor,
) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    DisposableEffect(Unit) {
        window?.statusBarColor = color.toArgb()

        onDispose {
            window?.statusBarColor = PrimaryBlackColor.toArgb()
        }
    }
}
