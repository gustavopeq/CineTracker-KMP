package features.onboarding.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.onboarding_welcome_bg
import cinetracker_kmp.composeapp.generated.resources.onboarding_welcome_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_welcome_title_app
import cinetracker_kmp.composeapp.generated.resources.onboarding_welcome_title_prefix
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryYellowColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val WELCOME_TITLE_FONT_SIZE = 42
private const val WELCOME_TITLE_LINE_HEIGHT = 46
private const val WELCOME_TITLE_LETTER_SPACING = -2f
private const val GRADIENT_TOP_HEIGHT = 128

@Composable
fun OnboardingWelcomeScreen(modifier: Modifier = Modifier) {
    val saturationMatrix = ColorMatrix().apply { setToSaturation(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.onboarding_welcome_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(saturationMatrix),
            modifier = Modifier.fillMaxSize()
        )

        // Dark diagonal gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlackColor.copy(alpha = 0.8f),
                            PrimaryBlackColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top gradient for status bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(GRADIENT_TOP_HEIGHT.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PrimaryBlackColor.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bottom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimaryBlackColor
                        )
                    )
                )
        )

        // Text content at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val prefix = stringResource(Res.string.onboarding_welcome_title_prefix)
            val appName = stringResource(Res.string.onboarding_welcome_title_app)
            val titleText = buildAnnotatedString {
                append(prefix)
                append("\n")
                withStyle(SpanStyle(color = PrimaryYellowColor)) {
                    append(appName)
                }
            }

            Text(
                text = titleText,
                fontSize = WELCOME_TITLE_FONT_SIZE.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = WELCOME_TITLE_LINE_HEIGHT.sp,
                letterSpacing = WELCOME_TITLE_LETTER_SPACING.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.onboarding_welcome_desc),
                fontSize = OnboardingConstants.DESC_FONT_SIZE.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = OnboardingConstants.DESC_LINE_HEIGHT.sp,
                color = OnboardingConstants.DESCRIPTION_COLOR,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
