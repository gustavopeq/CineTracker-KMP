package common.ui.components.bottomsheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_check
import common.util.UiConstants.SMALL_MARGIN
import org.jetbrains.compose.resources.painterResource

@Composable
fun SortButton(
    text: String,
    isSelected: Boolean = false,
    textColor: Color,
    onClick: () -> Unit,
) {
    Button(
        contentPadding = PaddingValues(horizontal = SMALL_MARGIN.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        onClick = onClick,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant else textColor,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_check),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}
