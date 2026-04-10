package features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import auth.model.AuthState
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.dialog_cancel
import cinetracker_kmp.composeapp.generated.resources.dialog_delete
import cinetracker_kmp.composeapp.generated.resources.dialog_keep
import cinetracker_kmp.composeapp.generated.resources.dialog_remove
import cinetracker_kmp.composeapp.generated.resources.settings_app_language
import cinetracker_kmp.composeapp.generated.resources.settings_delete_account
import cinetracker_kmp.composeapp.generated.resources.settings_delete_confirm
import cinetracker_kmp.composeapp.generated.resources.settings_delete_keep_data
import cinetracker_kmp.composeapp.generated.resources.settings_notifications
import cinetracker_kmp.composeapp.generated.resources.settings_region
import cinetracker_kmp.composeapp.generated.resources.settings_sign_in
import cinetracker_kmp.composeapp.generated.resources.settings_sign_out
import cinetracker_kmp.composeapp.generated.resources.settings_sign_out_confirm
import cinetracker_kmp.composeapp.generated.resources.settings_version
import com.projects.cinetracker.BuildKonfig
import common.ui.components.ClassicLoadingIndicator
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryRedColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.RETURN_TOP_BAR_HEIGHT
import common.util.UiConstants.SECTION_PADDING
import common.util.UiConstants.SETTINGS_AVATAR_BOTTOM_SPACING
import common.util.platform.rememberNotificationPermissionLauncher
import features.settings.events.SettingsEvent
import features.settings.ui.components.ProfileAvatar
import features.settings.ui.components.SettingsRow
import features.settings.ui.components.SettingsToggleRow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    goToLanguagePicker: () -> Unit,
    goToRegionPicker: () -> Unit,
    goToAuth: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val currentLanguage by viewModel.currentLanguageDisplay.collectAsState()
    val currentRegion by viewModel.currentRegionDisplay.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showKeepDataDialog by remember { mutableStateOf(false) }

    val isLoggedIn = authState is AuthState.LoggedIn
    val displayName = (authState as? AuthState.LoggedIn)?.displayName

    val requestPermission = rememberNotificationPermissionLauncher { granted ->
        viewModel.onEvent(SettingsEvent.NotificationPermissionResult(granted))
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = RETURN_TOP_BAR_HEIGHT.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatar()
        Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))
        if (isLoggedIn && !displayName.isNullOrBlank()) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryWhiteColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
            )
        } else {
            Text(
                text = stringResource(Res.string.settings_sign_in),
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryYellowColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = goToAuth)
            )
        }
        Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))


        HorizontalDivider(color = MaterialTheme.colorScheme.inverseSurface)

        SettingsRow(
            label = stringResource(Res.string.settings_app_language),
            value = currentLanguage,
            onClick = goToLanguagePicker
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
        )

        SettingsRow(
            label = stringResource(Res.string.settings_region),
            value = currentRegion,
            onClick = goToRegionPicker
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
        )

        SettingsToggleRow(
            label = stringResource(Res.string.settings_notifications),
            checked = notificationsEnabled,
            onToggle = { isEnabling ->
                if (isEnabling) {
                    requestPermission()
                } else {
                    viewModel.onEvent(SettingsEvent.DisableNotifications)
                }
            }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.inverseSurface)

        if (isLoggedIn) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
            )

            SettingsRow(
                label = stringResource(Res.string.settings_delete_account),
                value = "",
                onClick = { showDeleteDialog = true }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
            )

            SettingsRow(
                label = stringResource(Res.string.settings_sign_out),
                value = "",
                onClick = { showSignOutDialog = true }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "${stringResource(Res.string.settings_version)} v${BuildKonfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryGreyColor
        )

        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ClassicLoadingIndicator()
        }
    }
    } // Box

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(stringResource(Res.string.settings_sign_out)) },
            text = { Text(stringResource(Res.string.settings_sign_out_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(SettingsEvent.SignOut)
                    showSignOutDialog = false
                }) {
                    Text(stringResource(Res.string.settings_sign_out), color = PrimaryYellowColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(stringResource(Res.string.dialog_cancel), color = SecondaryGreyColor)
                }
            },
            containerColor = MainBarGreyColor,
            titleContentColor = PrimaryWhiteColor,
            textContentColor = PrimaryWhiteColor
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.settings_delete_account)) },
            text = { Text(stringResource(Res.string.settings_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    showKeepDataDialog = true
                }) {
                    Text(stringResource(Res.string.dialog_delete), color = PrimaryRedColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.dialog_cancel), color = SecondaryGreyColor)
                }
            },
            containerColor = MainBarGreyColor,
            titleContentColor = PrimaryWhiteColor,
            textContentColor = PrimaryWhiteColor
        )
    }

    if (showKeepDataDialog) {
        AlertDialog(
            onDismissRequest = { showKeepDataDialog = false },
            title = { Text(stringResource(Res.string.settings_delete_keep_data)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(SettingsEvent.DeleteAccount(keepLocalData = true))
                    showKeepDataDialog = false
                }) {
                    Text(stringResource(Res.string.dialog_keep), color = PrimaryYellowColor)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(SettingsEvent.DeleteAccount(keepLocalData = false))
                    showKeepDataDialog = false
                }) {
                    Text(stringResource(Res.string.dialog_remove), color = PrimaryRedColor)
                }
            },
            containerColor = MainBarGreyColor,
            titleContentColor = PrimaryWhiteColor,
            textContentColor = PrimaryWhiteColor
        )
    }
}
