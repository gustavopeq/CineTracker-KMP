package features.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.settings_language_picker_title
import common.util.UiConstants.DEFAULT_MARGIN
import features.settings.domain.SettingsInteractor
import features.settings.ui.components.PickerItemRow
import features.settings.ui.components.PickerTopBar
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LanguagePickerScreen(onBack: () -> Unit) {
    val settingsInteractor: SettingsInteractor = koinInject()
    val languages = remember { settingsInteractor.getSupportedLanguages() }
    val selectedTag = rememberSaveable { mutableStateOf(settingsInteractor.getAppLanguage()) }

    val initialTag = remember { settingsInteractor.getAppLanguage() }
    val orderedLanguages = remember {
        val sel = languages.find { it.tag == initialTag }
        val rest = languages.filter { it.tag != initialTag }.sortedBy { it.displayName }
        if (sel != null) listOf(sel) + rest else languages.sortedBy { it.displayName }
    }
    val hasInitialSelection = remember { languages.any { it.tag == initialTag } }

    val saveAndGoBack = remember(onBack) {
        {
            settingsInteractor.setAppLanguage(selectedTag.value)
            onBack()
        }
    }

    BackHandler { saveAndGoBack() }

    Column(modifier = Modifier.fillMaxSize()) {
        PickerTopBar(
            title = Res.string.settings_language_picker_title,
            onBack = saveAndGoBack
        )

        LazyColumn {
            itemsIndexed(orderedLanguages, key = { _, item -> item.tag }) { index, item ->
                PickerItemRow(
                    text = item.displayName,
                    isSelected = item.tag == selectedTag.value,
                    onClick = { selectedTag.value = item.tag }
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
