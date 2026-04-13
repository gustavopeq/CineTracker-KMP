package features.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.announcement_body
import cinetracker_kmp.composeapp.generated.resources.announcement_maybe_later
import cinetracker_kmp.composeapp.generated.resources.announcement_tag
import cinetracker_kmp.composeapp.generated.resources.announcement_title
import cinetracker_kmp.composeapp.generated.resources.announcement_title_accent
import cinetracker_kmp.composeapp.generated.resources.auth_create_account
import cinetracker_kmp.composeapp.generated.resources.ic_cloud
import cinetracker_kmp.composeapp.generated.resources.login_onboarding_bg
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryGreyColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.LARGE_PADDING
import common.util.UiConstants.SYSTEM_BOTTOM_NAV_PADDING
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val ANNOUNCEMENT_BOTTOM_SPACING = 58
private const val TAG_ICON_SIZE = 16
private const val TAG_INNER_SPACING = 6
private const val TAG_CORNER_RADIUS = 16
private const val TITLE_FONT_SIZE = 36
private const val BUTTON_HEIGHT = 56
private const val BUTTON_CORNER_RADIUS = 12
private const val BUTTON_FONT_SIZE = 18
private const val TAG_FONT_SIZE = 11

@Composable
fun AccountAnnouncementView(onCreateAccount: () -> Unit, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(PrimaryBlackColor)) {
        Image(
            painter = painterResource(Res.drawable.login_onboarding_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = SYSTEM_BOTTOM_NAV_PADDING.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            SecuredStorageTag()

            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))

            Text(
                text = stringResource(Res.string.announcement_title).uppercase(),
                fontSize = TITLE_FONT_SIZE.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = stringResource(Res.string.announcement_title_accent).uppercase(),
                fontSize = TITLE_FONT_SIZE.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = PrimaryYellowColor
            )

            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))

            Text(
                text = stringResource(Res.string.announcement_body),
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryGreyColor
            )

            Spacer(modifier = Modifier.height(SYSTEM_BOTTOM_NAV_PADDING.dp))

            Button(
                onClick = onCreateAccount,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BUTTON_HEIGHT.dp),
                shape = RoundedCornerShape(BUTTON_CORNER_RADIUS.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryYellowColor,
                    contentColor = PrimaryBlackColor
                )
            ) {
                Text(
                    text = stringResource(Res.string.auth_create_account).uppercase(),
                    fontSize = BUTTON_FONT_SIZE.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(DEFAULT_MARGIN.dp))

            Text(
                text = stringResource(Res.string.announcement_maybe_later).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryGreyColor,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
                    .padding(vertical = DEFAULT_MARGIN.dp)
            )

            Spacer(modifier = Modifier.height(ANNOUNCEMENT_BOTTOM_SPACING.dp))
        }
    }
}

@Composable
private fun SecuredStorageTag() {
    Row(
        modifier = Modifier
            .background(
                color = MainBarGreyColor,
                shape = RoundedCornerShape(TAG_CORNER_RADIUS.dp)
            )
            .padding(horizontal = LARGE_PADDING.dp, vertical = TAG_INNER_SPACING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_cloud),
            contentDescription = null,
            modifier = Modifier.size(TAG_ICON_SIZE.dp),
            tint = PrimaryYellowColor
        )
        Spacer(modifier = Modifier.width(TAG_INNER_SPACING.dp))
        Text(
            text = stringResource(Res.string.announcement_tag).uppercase(),
            fontSize = TAG_FONT_SIZE.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryYellowColor,
            letterSpacing = 1.sp
        )
    }
}
