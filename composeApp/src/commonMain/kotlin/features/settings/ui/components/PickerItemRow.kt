package features.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.SMALL_PADDING

@Composable
fun PickerItemRow(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = DEFAULT_MARGIN.dp, vertical = SMALL_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = PrimaryYellowColor,
                unselectedColor = SecondaryGreyColor
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) PrimaryYellowColor else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = SMALL_PADDING.dp)
        )
    }
}
