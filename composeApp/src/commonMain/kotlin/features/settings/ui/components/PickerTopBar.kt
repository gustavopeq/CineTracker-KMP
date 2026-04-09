package features.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.back_arrow_description
import cinetracker_kmp.composeapp.generated.resources.ic_back_arrow
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.RETURN_TOP_BAR_HEIGHT
import common.util.UiConstants.SETTINGS_BACK_ICON_SIZE
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PickerTopBar(title: StringResource, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RETURN_TOP_BAR_HEIGHT.dp)
            .padding(horizontal = DEFAULT_MARGIN.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_back_arrow),
            contentDescription = stringResource(resource = Res.string.back_arrow_description),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(SETTINGS_BACK_ICON_SIZE.dp)
                .clickable(onClick = onBack)
        )
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = DEFAULT_MARGIN.dp)
        )
    }
}
