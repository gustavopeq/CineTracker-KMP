package features.auth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_visibility
import cinetracker_kmp.composeapp.generated.resources.ic_visibility_off
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.CARD_ROUND_CORNER
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.FORM_FIELD_HEIGHT
import org.jetbrains.compose.resources.painterResource

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLength: Int = Int.MAX_VALUE,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape((CARD_ROUND_CORNER * 2).dp)
    val selectionColors = TextSelectionColors(
        handleColor = PrimaryYellowColor,
        backgroundColor = PrimaryYellowColor.copy(alpha = 0.3f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = PrimaryWhiteColor),
            cursorBrush = SolidColor(PrimaryYellowColor),
            interactionSource = interactionSource,
            modifier = modifier
                .fillMaxWidth()
                .height(FORM_FIELD_HEIGHT.dp)
                .background(MainBarGreyColor, shape)
                .then(
                    if (isFocused) {
                        Modifier.border(1.dp, PrimaryYellowColor, shape)
                    } else {
                        Modifier
                    }
                ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = DEFAULT_MARGIN.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = SecondaryGreyColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                    if (isPassword && onTogglePasswordVisibility != null) {
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
                }
            }
        )
    }
}
