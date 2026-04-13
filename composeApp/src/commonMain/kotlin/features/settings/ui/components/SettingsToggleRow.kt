package features.settings.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.components.AppSwitch
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.SETTINGS_ROW_HEIGHT

@Composable
fun SettingsToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SETTINGS_ROW_HEIGHT.dp)
            .padding(horizontal = DEFAULT_MARGIN.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(1f)
        )
        AppSwitch(
            checked = checked,
            onCheckedChange = onToggle
        )
    }
}
