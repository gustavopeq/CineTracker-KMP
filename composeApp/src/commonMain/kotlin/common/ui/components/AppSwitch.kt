package common.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = PrimaryYellowColor,
            checkedTrackColor = PrimaryBlackColor,
            checkedBorderColor = PrimaryYellowColor,
            uncheckedThumbColor = SecondaryGreyColor,
            uncheckedTrackColor = MainBarGreyColor,
            uncheckedBorderColor = SecondaryGreyColor
        )
    )
}
