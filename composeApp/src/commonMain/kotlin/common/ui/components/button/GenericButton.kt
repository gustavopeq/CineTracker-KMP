package common.ui.components.button

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import common.ui.theme.PrimaryYellowColor
import common.util.UiConstants.CLASSIC_BUTTON_BORDER_SIZE

@Composable
fun GenericButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(CLASSIC_BUTTON_BORDER_SIZE.dp),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = PrimaryYellowColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = buttonText,
                style = textStyle,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
