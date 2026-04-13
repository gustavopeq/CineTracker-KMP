package features.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_already_have_account
import cinetracker_kmp.composeapp.generated.resources.auth_create_account
import cinetracker_kmp.composeapp.generated.resources.auth_email_hint
import cinetracker_kmp.composeapp.generated.resources.auth_forgot_password
import cinetracker_kmp.composeapp.generated.resources.auth_name_hint
import cinetracker_kmp.composeapp.generated.resources.auth_no_account
import cinetracker_kmp.composeapp.generated.resources.auth_password_hint
import cinetracker_kmp.composeapp.generated.resources.auth_sign_in
import common.ui.components.button.GenericButton
import common.ui.theme.PrimaryRedColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.AUTH_NAME_MAX_LENGTH
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.FORM_FIELD_HEIGHT
import common.util.UiConstants.LARGE_MARGIN
import features.auth.events.AuthEvent
import features.auth.ui.components.AuthTextField
import features.settings.ui.components.PickerTopBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmailAuthScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val isCreateMode by viewModel.isCreateMode.collectAsState()
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val formError by viewModel.formError.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()

    val isFormValid = email.isNotBlank() &&
        password.isNotBlank() &&
        (!isCreateMode || name.isNotBlank())

    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        PickerTopBar(
            title = if (isCreateMode) {
                Res.string.auth_create_account
            } else {
                Res.string.auth_sign_in
            },
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DEFAULT_MARGIN.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isCreateMode) {
                Column {
                    AuthTextField(
                        value = name,
                        onValueChange = viewModel::updateName,
                        placeholder = stringResource(Res.string.auth_name_hint),
                        maxLength = AUTH_NAME_MAX_LENGTH
                    )
                    Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                }
            }

            AuthTextField(
                value = email,
                onValueChange = viewModel::updateEmail,
                placeholder = stringResource(Res.string.auth_email_hint),
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

            AuthTextField(
                value = password,
                onValueChange = viewModel::updatePassword,
                placeholder = stringResource(Res.string.auth_password_hint),
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = {
                    viewModel.onEvent(AuthEvent.TogglePasswordVisibility)
                }
            )

            Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

            GenericButton(
                modifier = Modifier.fillMaxWidth().height(FORM_FIELD_HEIGHT.dp),
                buttonText = stringResource(
                    if (isCreateMode) {
                        Res.string.auth_create_account
                    } else {
                        Res.string.auth_sign_in
                    }
                ),
                enabled = isFormValid,
                isLoading = isLoading,
                onClick = {
                    if (isCreateMode) {
                        viewModel.onEvent(AuthEvent.SignUpWithEmail)
                    } else {
                        viewModel.onEvent(AuthEvent.SignInWithEmail)
                    }
                }
            )

            formError?.let { error ->
                Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
                Text(
                    text = stringResource(error),
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryRedColor
                )
            }

            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))

            Text(
                text = stringResource(
                    if (isCreateMode) {
                        Res.string.auth_already_have_account
                    } else {
                        Res.string.auth_no_account
                    }
                ),
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryYellowColor,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { viewModel.onEvent(AuthEvent.ToggleMode) }
                    .padding(vertical = DEFAULT_MARGIN.dp)
            )

            if (!isCreateMode) {
                Text(
                    text = stringResource(Res.string.auth_forgot_password),
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryGreyColor,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onForgotPassword() }
                        .padding(vertical = DEFAULT_MARGIN.dp)
                )
            }
        }
    }
}
