package features.auth.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_google
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.PrimaryYellowColor
import common.util.UiConstants.CARD_ROUND_CORNER
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.FORM_FIELD_HEIGHT
import org.jetbrains.compose.resources.painterResource

@Composable
fun GoogleSignInButton(text: String, isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryWhiteColor,
            contentColor = PrimaryBlackColor
        ),
        shape = RoundedCornerShape(CARD_ROUND_CORNER.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(FORM_FIELD_HEIGHT.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = PrimaryYellowColor,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_google),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(DEFAULT_PADDING.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
