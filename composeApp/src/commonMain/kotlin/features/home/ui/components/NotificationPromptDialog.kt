package features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_notifications
import cinetracker_kmp.composeapp.generated.resources.notif_dialog_description
import cinetracker_kmp.composeapp.generated.resources.notif_dialog_enable
import cinetracker_kmp.composeapp.generated.resources.notif_dialog_skip
import cinetracker_kmp.composeapp.generated.resources.notif_dialog_title
import common.ui.components.button.GenericButton
import common.ui.components.button.SimpleButton
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryGreyColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.RoundCornerShapes
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.LARGE_MARGIN
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val BELL_ICON_SIZE = 80

@Composable
fun NotificationPromptDialog(onEnableReminders: () -> Unit, onSkip: () -> Unit) {
    Dialog(onDismissRequest = onSkip) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MainBarGreyColor, RoundCornerShapes.large)
                .padding(LARGE_MARGIN.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DEFAULT_MARGIN.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_notifications),
                contentDescription = null,
                tint = PrimaryYellowColor,
                modifier = Modifier.size(BELL_ICON_SIZE.dp)
            )
            Text(
                text = stringResource(Res.string.notif_dialog_title),
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryWhiteColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(Res.string.notif_dialog_description),
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryGreyColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
            GenericButton(
                buttonText = stringResource(Res.string.notif_dialog_enable),
                onClick = onEnableReminders
            )
            SimpleButton(
                text = stringResource(Res.string.notif_dialog_skip),
                onClick = onSkip
            )
        }
    }
}
