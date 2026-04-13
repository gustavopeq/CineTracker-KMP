package features.settings.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_edit
import common.ui.theme.MainBarGreyColor
import common.ui.theme.PrimaryWhiteColor
import common.ui.theme.PrimaryYellowColor
import common.util.UiConstants.PROFILE_AVATAR_SIZE
import features.settings.ui.model.getDrawableForAvatarKey
import org.jetbrains.compose.resources.painterResource

private const val EDIT_ICON_SIZE = 32
private const val EDIT_ICON_PADDING = 6

@Composable
fun ProfileAvatar(
    avatarKey: String,
    modifier: Modifier = Modifier,
    showEditIcon: Boolean = false,
    onEditClick: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(PROFILE_AVATAR_SIZE.dp)
                .clip(CircleShape)
                .border(width = 1.dp, color = PrimaryYellowColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(getDrawableForAvatarKey(avatarKey)),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
        if (showEditIcon && onEditClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(EDIT_ICON_SIZE.dp)
                    .clip(CircleShape)
                    .background(MainBarGreyColor)
                    .border(width = 1.dp, color = PrimaryYellowColor, CircleShape)
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = null,
                    tint = PrimaryWhiteColor,
                    modifier = Modifier
                        .size(EDIT_ICON_SIZE.dp)
                        .padding(EDIT_ICON_PADDING.dp)
                )
            }
        }
    }
}
