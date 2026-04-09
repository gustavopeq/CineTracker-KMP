package features.auth.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_visibility
import cinetracker_kmp.composeapp.generated.resources.ic_visibility_off
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.CARD_ROUND_CORNER
import org.jetbrains.compose.resources.painterResource

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = SecondaryGreyColor)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
            {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        painter = painterResource(
                            if (isPasswordVisible) {
                                Res.drawable.ic_visibility_off
                            } else {
                                Res.drawable.ic_visibility
                            }
                        ),
                        contentDescription = null,
                        tint = SecondaryGreyColor
                    )
                }
            }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = PrimaryWhiteColor,
            unfocusedTextColor = PrimaryWhiteColor,
            focusedContainerColor = MainBarGreyColor,
            unfocusedContainerColor = MainBarGreyColor,
            cursorColor = PrimaryWhiteColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape((CARD_ROUND_CORNER * 2).dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth()
    )
}
