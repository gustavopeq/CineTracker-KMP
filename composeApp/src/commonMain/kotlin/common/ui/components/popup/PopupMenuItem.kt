package common.ui.components.popup

import androidx.compose.ui.graphics.Color

data class PopupMenuItem(
    val title: String,
    val textColor: Color = Color.White,
    val onClick: () -> Unit,
)
