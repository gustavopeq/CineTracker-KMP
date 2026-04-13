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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.auth_back_to_sign_in
import cinetracker_kmp.composeapp.generated.resources.auth_confirm_password_hint
import cinetracker_kmp.composeapp.generated.resources.auth_new_password_hint
import cinetracker_kmp.composeapp.generated.resources.auth_new_password_subtitle
import cinetracker_kmp.composeapp.generated.resources.auth_new_password_title
import cinetracker_kmp.composeapp.generated.resources.auth_password_updated
import cinetracker_kmp.composeapp.generated.resources.auth_save_password
import common.ui.components.button.GenericButton
import common.ui.theme.PrimaryRedColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.FORM_FIELD_HEIGHT
import common.util.UiConstants.LARGE_MARGIN
import features.auth.ui.components.AuthTextField
import features.settings.ui.components.PickerTopBar
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewPasswordScreen(onDone: () -> Unit) {
    val viewModel: NewPasswordViewModel = koinViewModel()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isConfirmPasswordVisible by viewModel.isConfirmPasswordVisible.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        PickerTopBar(
            title = Res.string.auth_new_password_title,
            onBack = onDone
        )

        AnimatedContent(targetState = isSuccess) { success ->
            if (success) {
                SuccessContent(onDone = onDone)
            } else {
                FormContent(
                    password = password,
                    confirmPassword = confirmPassword,
                    isPasswordVisible = isPasswordVisible,
                    isConfirmPasswordVisible = isConfirmPasswordVisible,
                    isLoading = isLoading,
                    error = error,
                    onPasswordChange = viewModel::updatePassword,
                    onConfirmPasswordChange = viewModel::updateConfirmPassword,
                    onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
                    onToggleConfirmPasswordVisibility = viewModel::toggleConfirmPasswordVisibility,
                    onSubmit = viewModel::submit
                )
            }
        }
    }
}

@Composable
private fun FormContent(
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    isLoading: Boolean,
    error: StringResource?,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DEFAULT_MARGIN.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

        Text(
            text = stringResource(Res.string.auth_new_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryGreyColor
        )

        Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

        AuthTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(Res.string.auth_new_password_hint),
            isPassword = true,
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility
        )

        Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

        AuthTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(Res.string.auth_confirm_password_hint),
            isPassword = true,
            isPasswordVisible = isConfirmPasswordVisible,
            onTogglePasswordVisibility = onToggleConfirmPasswordVisibility
        )

        Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

        GenericButton(
            modifier = Modifier.fillMaxWidth().height(FORM_FIELD_HEIGHT.dp),
            buttonText = stringResource(Res.string.auth_save_password),
            enabled = password.isNotBlank() && confirmPassword.isNotBlank(),
            isLoading = isLoading,
            onClick = onSubmit
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))
            Text(
                text = stringResource(error),
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryRedColor
            )
        }
    }
}

@Composable
private fun SuccessContent(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DEFAULT_MARGIN.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = stringResource(Res.string.auth_password_updated),
            style = MaterialTheme.typography.headlineMedium,
            color = PrimaryYellowColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LARGE_MARGIN.dp))

        GenericButton(
            modifier = Modifier.fillMaxWidth().height(FORM_FIELD_HEIGHT.dp),
            buttonText = stringResource(Res.string.auth_back_to_sign_in),
            onClick = onDone
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
