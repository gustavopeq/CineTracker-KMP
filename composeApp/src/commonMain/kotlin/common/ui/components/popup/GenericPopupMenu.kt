package common.ui.components.popup

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GenericPopupMenu(
    showMenu: Boolean,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    onDismissRequest: () -> Unit,
    menuItems: List<PopupMenuItem>,
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(color = backgroundColor),
    ) {
        menuItems.forEach { menuItem ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = menuItem.title,
                        color = menuItem.textColor,
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                onClick = {
                    menuItem.onClick()
                    onDismissRequest()
                },
            )
        }
    }
}
