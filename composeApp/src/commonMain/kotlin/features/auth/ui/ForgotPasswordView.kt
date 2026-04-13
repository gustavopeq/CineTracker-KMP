package features.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_back_to_sign_in
import cinetracker_kmp.composeapp.generated.resources.auth_email_hint
import cinetracker_kmp.composeapp.generated.resources.auth_reset_email_sent
import cinetracker_kmp.composeapp.generated.resources.auth_reset_email_sent_detail
import cinetracker_kmp.composeapp.generated.resources.auth_reset_password_subtitle
import cinetracker_kmp.composeapp.generated.resources.auth_reset_password_title
import cinetracker_kmp.composeapp.generated.resources.auth_send_reset_link
import common.ui.components.button.GenericButton
import common.ui.theme.PrimaryRedColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.FORM_FIELD_HEIGHT
import common.util.UiConstants.LARGE_MARGIN
import features.auth.events.AuthEvent
import features.auth.ui.components.AuthTextField
import features.settings.ui.components.PickerTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgotPasswordScreen(viewModel: AuthViewModel, onBack: () -> Unit) {
    val email by viewModel.email.collectAsState()
    val resetState by viewModel.resetPasswordState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearResetPasswordState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        PickerTopBar(
            title = Res.string.auth_reset_password_title,
            onBack = onBack
        )

        AnimatedContent(
            targetState = resetState is ResetPasswordState.Success
        ) { isSuccess ->
            if (isSuccess) {
                SuccessContent(onBack = onBack)
            } else {
                FormContent(
                    email = email,
                    onEmailChange = viewModel::updateEmail,
                    isLoading = resetState is ResetPasswordState.Loading,
                    error = resetState as? ResetPasswordState.Error,
                    onSendResetLink = {
                        viewModel.onEvent(AuthEvent.ResetPassword)
                    }
                )
            }
        }
    }
}

@Composable
private fun FormContent(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    error: ResetPasswordState.Error?,
    onSendResetLink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DEFAULT_MARGIN.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

        Text(
            text = stringResource(Res.string.auth_reset_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryGreyColor
        )

        Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(Res.string.auth_email_hint),
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

        GenericButton(
            modifier = Modifier.fillMaxWidth().height(FORM_FIELD_HEIGHT.dp),
            buttonText = stringResource(Res.string.auth_send_reset_link),
            enabled = email.isNotBlank(),
            isLoading = isLoading,
            onClick = onSendResetLink
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
            Text(
                text = stringResource(error.message),
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryRedColor
            )
        }
    }
}

@Composable
private fun SuccessContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DEFAULT_MARGIN.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = stringResource(Res.string.auth_reset_email_sent),
            style = MaterialTheme.typography.headlineMedium,
            color = PrimaryYellowColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

        Text(
            text = stringResource(Res.string.auth_reset_email_sent_detail),
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryGreyColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

        GenericButton(
            modifier = Modifier.fillMaxWidth().height(FORM_FIELD_HEIGHT.dp),
            buttonText = stringResource(Res.string.auth_back_to_sign_in),
            onClick = onBack
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
