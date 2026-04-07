package features.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

    val initialCode = remember { settingsInteractor.getAppRegion() }
    val orderedRegions = remember {
        val sel = regions.find { it.code == initialCode }
        val rest = regions.filter { it.code != initialCode }
        if (sel != null) listOf(sel) + rest else regions
    }
    val hasInitialSelection = remember { regions.any { it.code == initialCode } }

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

        LazyColumn {
            itemsIndexed(orderedRegions, key = { _, item -> item.code }) { index, item ->
                PickerItemRow(
                    text = item.displayName,
                    isSelected = item.code == selectedCode.value,
                    onClick = { selectedCode.value = item.code }
                )
                if (index == 0 && hasInitialSelection) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
                    )
                }
            }
        }
    }
}
