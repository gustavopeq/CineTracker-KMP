package features.announcement.ui

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.announcement_benefit_free
import cinetracker_kmp.composeapp.generated.resources.announcement_benefit_restore
import cinetracker_kmp.composeapp.generated.resources.announcement_benefit_safe
import cinetracker_kmp.composeapp.generated.resources.announcement_maybe_later
import cinetracker_kmp.composeapp.generated.resources.announcement_reassurance
import cinetracker_kmp.composeapp.generated.resources.announcement_title
import cinetracker_kmp.composeapp.generated.resources.auth_create_account
import cinetracker_kmp.composeapp.generated.resources.ic_star
import cinetracker_kmp.composeapp.generated.resources.space_bg
import common.ui.theme.PrimaryBlackColor
import common.ui.theme.PrimaryGreyColor
import common.ui.theme.PrimaryYellowColor
import common.ui.theme.SecondaryGreyColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AccountAnnouncementView(
    onCreateAccount: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PrimaryBlackColor)) {
        Image(
            painter = painterResource(Res.drawable.space_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.3f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Icon(
            painter = painterResource(Res.drawable.ic_star),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = PrimaryYellowColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(Res.string.announcement_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        BenefitRow(stringResource(Res.string.announcement_benefit_safe))
        BenefitRow(stringResource(Res.string.announcement_benefit_restore))
        BenefitRow(stringResource(Res.string.announcement_benefit_free))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.announcement_reassurance),
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryGreyColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onCreateAccount,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryYellowColor,
                contentColor = PrimaryBlackColor
            )
        ) {
            Text(
                text = stringResource(Res.string.auth_create_account),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.announcement_maybe_later),
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryGreyColor,
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
                .padding(vertical = 16.dp)
        )
        }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\u2022",
            style = MaterialTheme.typography.headlineMedium,
            color = PrimaryYellowColor,
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
