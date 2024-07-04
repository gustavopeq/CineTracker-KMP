package common.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.util.UiConstants.SYSTEM_BOTTOM_NAV_PADDING

@Composable
fun SystemNavBarSpacer() {
    Spacer(modifier = Modifier.height(SYSTEM_BOTTOM_NAV_PADDING.dp))
}
