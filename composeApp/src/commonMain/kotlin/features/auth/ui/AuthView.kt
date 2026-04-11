package features.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.app_logo_image_description
import cinetracker_kmp.composeapp.generated.resources.auth_continue_email
import cinetracker_kmp.composeapp.generated.resources.auth_continue_google
import cinetracker_kmp.composeapp.generated.resources.auth_continue_without_account
import cinetracker_kmp.composeapp.generated.resources.auth_sign_in_title
import cinetracker_kmp.composeapp.generated.resources.cinetracker_name_logo
import cinetracker_kmp.composeapp.generated.resources.ic_mail
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.CARD_ROUND_CORNER
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.FORM_FIELD_HEIGHT
import common.util.UiConstants.SECTION_PADDING
import features.auth.events.AuthEvent
import features.auth.ui.components.GoogleSignInButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    goToEmailAuth: () -> Unit,
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarError by viewModel.snackbarError.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarErrorText = snackbarError?.let { stringResource(it) }

    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            onAuthSuccess()
        }
    }

    LaunchedEffect(snackbarError) {
        if (snackbarErrorText != null) {
            snackbarHostState.showSnackbar(snackbarErrorText)
            viewModel.onEvent(AuthEvent.DismissError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryYellowColor.copy(alpha = 0.2f),
                        PrimaryYellowColor.copy(alpha = 0.2f),
                        PrimaryBlackColor
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = MainBarGreyColor,
                            contentColor = PrimaryWhiteColor
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = DEFAULT_MARGIN.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.weight(0.25f))

            Image(
                painter = painterResource(Res.drawable.cinetracker_name_logo),
                contentDescription = stringResource(Res.string.app_logo_image_description),
                modifier = Modifier.size(275.dp)
            )

            Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

            Text(
                text = stringResource(Res.string.auth_sign_in_title),
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryWhiteColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

            OutlinedButton(
                onClick = goToEmailAuth,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryWhiteColor
                ),
                border = BorderStroke(1.dp, SecondaryGreyColor),
                shape = RoundedCornerShape(CARD_ROUND_CORNER.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FORM_FIELD_HEIGHT.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_mail),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(DEFAULT_PADDING.dp))
                Text(
                    text = stringResource(Res.string.auth_continue_email),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

            GoogleSignInButton(
                text = stringResource(Res.string.auth_continue_google),
                isLoading = isLoading,
                onClick = { viewModel.onEvent(AuthEvent.SignInWithGoogle) }
            )

            Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

            Text(
                text = stringResource(Res.string.auth_continue_without_account),
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryGreyColor,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
                    .padding(DEFAULT_MARGIN.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }
        }
    }
}
