package features.watchlist.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.delete_list_pop_up_item
import common.ui.components.popup.GenericPopupMenu
import common.ui.components.popup.PopupMenuItem
import common.ui.theme.MainBarGreyColor
import org.jetbrains.compose.resources.stringResource

@Composable
fun ListRemovePopUpMenu(
    showRemoveMenu: Boolean,
    menuOffset: Offset,
    onRemoveList: () -> Unit,
    onDismiss: () -> Unit,
) {
    val menuItems = listOf(
        PopupMenuItem(
            stringResource(resource = Res.string.delete_list_pop_up_item),
            onClick = onRemoveList,
        ),
    )
    Box(
        modifier = Modifier
            .absoluteOffset(x = (menuOffset.x / 2).dp),
    ) {
        GenericPopupMenu(
            showMenu = showRemoveMenu,
            backgroundColor = MainBarGreyColor,
            onDismissRequest = onDismiss,
            menuItems = menuItems,
        )
    }
}
