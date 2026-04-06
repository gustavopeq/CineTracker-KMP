package features.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.settings_region_picker_title
import common.util.UiConstants.DEFAULT_MARGIN
import features.settings.domain.SettingsInteractor
import features.settings.ui.components.PickerItemRow
import features.settings.ui.components.PickerTopBar
import org.koin.compose.koinInject

@Composable
fun RegionPickerScreen(onBack: () -> Unit) {
    val settingsInteractor: SettingsInteractor = koinInject()
    val regions = remember { settingsInteractor.getSupportedRegions() }
    val selectedCode = remember { mutableStateOf(settingsInteractor.getAppRegion()) }

    DisposableEffect(Unit) {
        onDispose {
            settingsInteractor.setAppRegion(selectedCode.value)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PickerTopBar(
            title = Res.string.settings_region_picker_title,
            onBack = onBack
        )

        val selectedItem = regions.find { it.code == selectedCode.value }
        val otherItems = regions.filter { it.code != selectedCode.value }

        LazyColumn {
            if (selectedItem != null) {
                item {
                    PickerItemRow(
                        text = selectedItem.displayName,
                        isSelected = true,
                        onClick = {}
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
                    )
                }
            }

            items(otherItems, key = { it.code }) { item ->
                PickerItemRow(
                    text = item.displayName,
                    isSelected = false,
                    onClick = { selectedCode.value = item.code }
                )
            }
        }
    }
}
