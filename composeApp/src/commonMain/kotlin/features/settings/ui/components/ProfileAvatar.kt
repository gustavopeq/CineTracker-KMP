package features.settings.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.avatar
import common.ui.theme.MainBarGreyColor
import common.util.UiConstants.PROFILE_AVATAR_IMAGE_FRACTION
import common.util.UiConstants.PROFILE_AVATAR_SIZE
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(PROFILE_AVATAR_SIZE.dp)
            .clip(CircleShape)
            .background(MainBarGreyColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(fraction = PROFILE_AVATAR_IMAGE_FRACTION),
            painter = painterResource(Res.drawable.avatar),
            contentDescription = null
        )
    }
}
