package features.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.app_logo_image_description
import cinetracker_kmp.composeapp.generated.resources.auth_continue_email
import cinetracker_kmp.composeapp.generated.resources.auth_continue_google
import cinetracker_kmp.composeapp.generated.resources.auth_continue_without_account
import cinetracker_kmp.composeapp.generated.resources.auth_or_divider
import cinetracker_kmp.composeapp.generated.resources.auth_sign_in_subtitle
import cinetracker_kmp.composeapp.generated.resources.auth_sign_in_title
import cinetracker_kmp.composeapp.generated.resources.cinetracker_logo_only
import cinetracker_kmp.composeapp.generated.resources.cinetracker_name_only
import cinetracker_kmp.composeapp.generated.resources.ic_mail
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.AUTH_DESCRIPTION_SPACING
import common.util.UiConstants.AUTH_LOGO_MAX_SIZE
import common.util.UiConstants.AUTH_LOGO_MIN_SIZE
import common.util.UiConstants.AUTH_LOGO_NAME_SPACING
import common.util.UiConstants.AUTH_LOGO_WIDTH_FRACTION
import common.util.UiConstants.AUTH_NAME_MAX_WIDTH
import common.util.UiConstants.AUTH_NAME_WIDTH_FRACTION
import common.util.UiConstants.CARD_ROUND_CORNER
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.FORM_FIELD_HEIGHT
import common.util.UiConstants.SECTION_PADDING
import common.util.platform.getScreenSizeInfo
import features.auth.events.AuthEvent
import features.auth.ui.components.GoogleSignInButton
import kotlin.math.min
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthScreen(viewModel: AuthViewModel, goToEmailAuth: () -> Unit, onDismiss: () -> Unit, onAuthSuccess: () -> Unit) {
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
            val screenWidthDp = getScreenSizeInfo().widthDp.value

            val logoSize = min(
                screenWidthDp * AUTH_LOGO_WIDTH_FRACTION,
                AUTH_LOGO_MAX_SIZE.toFloat()
            ).coerceAtLeast(AUTH_LOGO_MIN_SIZE.toFloat()).dp

            val nameWidth = min(
                screenWidthDp * AUTH_NAME_WIDTH_FRACTION,
                AUTH_NAME_MAX_WIDTH.toFloat()
            ).dp

            Spacer(modifier = Modifier.weight(1.5f))

            Image(
                painter = painterResource(Res.drawable.cinetracker_logo_only),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(logoSize).clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(AUTH_LOGO_NAME_SPACING.dp))

            Image(
                painter = painterResource(Res.drawable.cinetracker_name_only),
                contentDescription = stringResource(Res.string.app_logo_image_description),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.width(nameWidth)
            )

            Spacer(modifier = Modifier.height(AUTH_DESCRIPTION_SPACING.dp))

            Text(
                text = stringResource(Res.string.auth_sign_in_title),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryWhiteColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(DEFAULT_PADDING.dp))

            Text(
                text = stringResource(Res.string.auth_sign_in_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryGreyColor,
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

            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SecondaryGreyColor)
                Text(
                    text = stringResource(Res.string.auth_or_divider),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryGreyColor,
                    modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = SecondaryGreyColor)
            }

            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))

            GoogleSignInButton(
                text = stringResource(Res.string.auth_continue_google),
                isLoading = isLoading,
                onClick = { viewModel.onEvent(AuthEvent.SignInWithGoogle) }
            )

            Spacer(modifier = Modifier.weight(1f))

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

            Spacer(modifier = Modifier.height(SECTION_PADDING.dp))
        }
    }
}
