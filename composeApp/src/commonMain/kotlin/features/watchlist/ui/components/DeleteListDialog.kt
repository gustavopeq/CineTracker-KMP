package features.watchlist.ui.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.delete_list_display_body
import cinetracker_kmp.composeapp.generated.resources.delete_list_display_confirm_btn
import cinetracker_kmp.composeapp.generated.resources.delete_list_display_dismiss_btn
import cinetracker_kmp.composeapp.generated.resources.delete_list_display_title
import common.ui.components.button.SimpleButton
import common.ui.theme.MainBarGreyColor
import common.util.Constants.UNSELECTED_OPTION_INDEX
import common.util.UiConstants.CARD_ROUND_CORNER
import features.watchlist.events.WatchlistEvent
import features.watchlist.ui.WatchlistViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteListDialog(
    displayDeleteDialog: Boolean,
    listToRemoveIndex: MutableIntState,
    viewModel: WatchlistViewModel,
    tabList: List<WatchlistTabItem>,
    onDialogDismiss: () -> Unit,
) {
    if (displayDeleteDialog) {
        AlertDialog(
            title = {
                Text(
                    text = stringResource(resource = Res.string.delete_list_display_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            },
            text = {
                Text(
                    text = stringResource(resource = Res.string.delete_list_display_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surface,
                )
            },
            confirmButton = {
                RemoveDialogButton(
                    text = stringResource(resource = Res.string.delete_list_display_confirm_btn),
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        if (listToRemoveIndex.intValue != UNSELECTED_OPTION_INDEX) {
                            viewModel.onEvent(
                                WatchlistEvent.DeleteList(
                                    tabList[listToRemoveIndex.intValue].listId,
                                ),
                            )
                        }
                        onDialogDismiss()
                    },
                )
            },
            dismissButton = {
                RemoveDialogButton(
                    text = stringResource(resource = Res.string.delete_list_display_dismiss_btn),
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        onDialogDismiss()
                    },
                )
            },
            onDismissRequest = {
                onDialogDismiss()
            },
            containerColor = MainBarGreyColor,
            shape = RoundedCornerShape(CARD_ROUND_CORNER.dp),
        )
    }
}

@Composable
private fun RemoveDialogButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    SimpleButton(
        modifier = Modifier.offset(y = 15.dp),
        text = text,
        textColor = textColor,
        onClick = onClick,
    )
}
