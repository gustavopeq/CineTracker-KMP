package common.ui.components.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.snackbar_undo_text
import common.util.UiConstants.CARD_ROUND_CORNER
import common.util.UiConstants.DEFAULT_PADDING
import org.jetbrains.compose.resources.stringResource

@Composable
fun ClassicSnackbar(
    snackbarHostState: SnackbarHostState,
    onActionClick: (() -> Unit)? = null,
    screenContent: @Composable () -> Unit,
) {
    val textStyle = MaterialTheme.typography.bodyMedium
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        modifier = Modifier.padding(DEFAULT_PADDING.dp),
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(CARD_ROUND_CORNER.dp),
                        action = {
                            if (onActionClick != null) {
                                UndoActionButton(
                                    onActionClick = {
                                        onActionClick()
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                    },
                                    textStyle = textStyle,
                                )
                            }
                        },
                    ) {
                        Text(
                            text = snackbarData.visuals.message,
                            style = textStyle,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            screenContent()
        }
    }
}

@Composable
private fun UndoActionButton(
    onActionClick: () -> Unit,
    textStyle: TextStyle,
) {
    TextButton(onClick = { onActionClick() }) {
        Text(
            text = stringResource(resource = Res.string.snackbar_undo_text),
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}
