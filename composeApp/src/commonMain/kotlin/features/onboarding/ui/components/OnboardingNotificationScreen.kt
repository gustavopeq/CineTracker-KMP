package features.onboarding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_notifications
import cinetracker_kmp.composeapp.generated.resources.onboarding_notifications_desc
import cinetracker_kmp.composeapp.generated.resources.onboarding_notifications_enable
import cinetracker_kmp.composeapp.generated.resources.onboarding_notifications_skip
import cinetracker_kmp.composeapp.generated.resources.onboarding_notifications_title
import common.ui.theme.OnboardingButtonTextColor
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryGreyColor
import common.ui.theme.PrimaryYellowColor
import features.onboarding.ui.components.OnboardingConstants.DESC_FONT_SIZE
import features.onboarding.ui.components.OnboardingConstants.DESC_LINE_HEIGHT
import features.onboarding.ui.components.OnboardingConstants.DESCRIPTION_COLOR
import features.onboarding.ui.components.OnboardingConstants.SCREEN_HORIZONTAL_PADDING
import features.onboarding.ui.components.OnboardingConstants.TITLE_DESC_SPACING
import features.onboarding.ui.components.OnboardingConstants.TITLE_FONT_SIZE
import features.onboarding.ui.components.OnboardingConstants.TITLE_LETTER_SPACING
import features.onboarding.ui.components.OnboardingConstants.TITLE_LINE_HEIGHT
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val ICON_SIZE = 80
private const val ICON_BOTTOM_SPACING = 24
private const val BUTTON_HEIGHT = 56
private const val BUTTON_CORNER_RADIUS = 12
private const val BUTTON_FONT_SIZE = 18
private const val SKIP_TOP_SPACING = 16
private const val SKIP_FONT_SIZE = 16

@Composable
fun OnboardingNotificationScreen(
    onEnableReminders: () -> Unit,
    onSkip: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlackColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = SCREEN_HORIZONTAL_PADDING.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_notifications),
                contentDescription = null,
                tint = PrimaryYellowColor,
                modifier = Modifier.size(ICON_SIZE.dp)
            )

            Spacer(modifier = Modifier.height(ICON_BOTTOM_SPACING.dp))

            Text(
                text = stringResource(Res.string.onboarding_notifications_title),
                fontSize = TITLE_FONT_SIZE.sp,
                lineHeight = TITLE_LINE_HEIGHT.sp,
                letterSpacing = TITLE_LETTER_SPACING.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(TITLE_DESC_SPACING.dp))

            Text(
                text = stringResource(Res.string.onboarding_notifications_desc),
                fontSize = DESC_FONT_SIZE.sp,
                lineHeight = DESC_LINE_HEIGHT.sp,
                color = DESCRIPTION_COLOR,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(ICON_BOTTOM_SPACING.dp))

            val shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp)
            Button(
                onClick = onEnableReminders,
                shape = shape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BUTTON_HEIGHT.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryYellowColor,
                                PrimaryYellowColor.copy(alpha = 0.85f)
                            )
                        ),
                        shape = shape
                    )
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_notifications_enable),
                    fontSize = BUTTON_FONT_SIZE.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingButtonTextColor,
                )
            }

            Spacer(modifier = Modifier.height(SKIP_TOP_SPACING.dp))

            Text(
                text = stringResource(Res.string.onboarding_notifications_skip),
                fontSize = SKIP_FONT_SIZE.sp,
                color = PrimaryGreyColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = onSkip)
            )
        }
    }
}
