package features.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.settings_avatar_picker_title
import common.ui.theme.PrimaryYellowColor
import common.util.UiConstants.DEFAULT_MARGIN
import common.util.UiConstants.SECTION_PADDING
import features.settings.domain.SettingsInteractor
import features.settings.ui.components.PickerTopBar
import features.settings.ui.components.ProfileAvatar
import features.settings.ui.model.AvatarItem
import features.settings.ui.model.getAvatarsByCategory
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

private const val AVATAR_GRID_COLUMNS = 4

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AvatarPickerScreen(onBack: () -> Unit) {
    val settingsInteractor: SettingsInteractor = koinInject()
    val allAvatars = remember { getAvatarsByCategory().values.flatten() }
    val selectedKey = rememberSaveable { mutableStateOf(settingsInteractor.getUserAvatar()) }

    val saveAndGoBack = remember(onBack) {
        {
            settingsInteractor.setUserAvatar(selectedKey.value)
            onBack()
        }
    }

    BackHandler { saveAndGoBack() }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PickerTopBar(
            title = Res.string.settings_avatar_picker_title,
            onBack = saveAndGoBack
        )

        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

        ProfileAvatar(avatarKey = selectedKey.value)

        Spacer(modifier = Modifier.height(SECTION_PADDING.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(AVATAR_GRID_COLUMNS),
            horizontalArrangement = Arrangement.spacedBy(DEFAULT_MARGIN.dp),
            verticalArrangement = Arrangement.spacedBy(DEFAULT_MARGIN.dp),
            modifier = Modifier.padding(horizontal = DEFAULT_MARGIN.dp)
        ) {
            items(allAvatars, key = { it.key }) { avatar ->
                AvatarGridItem(
                    avatar = avatar,
                    isSelected = avatar.key == selectedKey.value,
                    onSelect = { selectedKey.value = avatar.key }
                )
            }
        }
    }
}

@Composable
private fun AvatarGridItem(
    avatar: AvatarItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Image(
        painter = painterResource(avatar.drawableRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = PrimaryYellowColor,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onSelect)
    )
}
