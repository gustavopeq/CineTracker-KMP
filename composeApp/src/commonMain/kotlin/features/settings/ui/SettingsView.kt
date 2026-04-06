package features.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.settings_app_language
import cinetracker_kmp.composeapp.generated.resources.settings_notifications
import cinetracker_kmp.composeapp.generated.resources.settings_region
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.RETURN_TOP_BAR_HEIGHT
import common.util.UiConstants.SECTION_PADDING
import common.util.platform.rememberNotificationPermissionLauncher
import features.settings.ui.components.ProfileAvatar
import features.settings.ui.components.SettingsRow
import features.settings.ui.components.SettingsToggleRow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    goToLanguagePicker: () -> Unit,
    goToRegionPicker: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        viewModel.refreshSettings()
    }

    SettingsContent(
        viewModel = viewModel,
        goToLanguagePicker = goToLanguagePicker,
        goToRegionPicker = goToRegionPicker
    )
}

@Composable
private fun SettingsContent(
    viewModel: SettingsViewModel,
    goToLanguagePicker: () -> Unit,
    goToRegionPicker: () -> Unit
) {
    val currentLanguage by viewModel.currentLanguageDisplay.collectAsState()
    val currentRegion by viewModel.currentRegionDisplay.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    val requestPermission = rememberNotificationPermissionLauncher { granted ->
        viewModel.onNotificationPermissionResult(granted)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = RETURN_TOP_BAR_HEIGHT.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

        ProfileAvatar()

        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

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
                    viewModel.disableNotifications()
                }
            }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.inverseSurface)
    }
}
