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
import cinetracker_kmp.composeapp.generated.resources.settings_language_picker_title
import common.util.UiConstants.DEFAULT_MARGIN
import features.settings.domain.SettingsInteractor
import features.settings.ui.components.PickerItemRow
import features.settings.ui.components.PickerTopBar
import org.koin.compose.koinInject

@Composable
fun LanguagePickerScreen(onBack: () -> Unit) {
    val settingsInteractor: SettingsInteractor = koinInject()
    val languages = remember { settingsInteractor.getSupportedLanguages() }
    val selectedTag = remember { mutableStateOf(settingsInteractor.getAppLanguage()) }

    DisposableEffect(Unit) {
        onDispose {
            settingsInteractor.setAppLanguage(selectedTag.value)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PickerTopBar(
            title = Res.string.settings_language_picker_title,
            onBack = onBack
        )

        val selectedItem = languages.find { it.tag == selectedTag.value }
        val otherItems = languages.filter { it.tag != selectedTag.value }.sortedBy { it.displayName }

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

            items(otherItems, key = { it.tag }) { item ->
                PickerItemRow(
                    text = item.displayName,
                    isSelected = false,
                    onClick = { selectedTag.value = item.tag }
                )
            }
        }
    }
}
