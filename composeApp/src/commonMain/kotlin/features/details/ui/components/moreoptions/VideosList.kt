package features.details.ui.components.moreoptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cinetracker_kmp.composeapp.generated.resources.Res
import cinetracker_kmp.composeapp.generated.resources.ic_play_video
import common.domain.models.content.Videos
import common.ui.components.NetworkImage
import common.ui.theme.MainBarGreyColor
import common.util.Constants.BASE_URL_YOUTUBE_VIDEO
import common.util.Constants.BASE_YOUTUBE_THUMBAIL_URL
import common.util.Constants.YOUTUBE_THUMBAIL_RESOLUTION
import common.util.UiConstants.DEFAULT_PADDING
import common.util.UiConstants.SMALL_PADDING
import common.util.UiConstants.VIDEOS_BORDER_SIZE
import common.util.UiConstants.VIDEOS_PLAY_ICON_SIZE
import org.jetbrains.compose.resources.painterResource

@Composable
fun VideoList(
    videoList: List<Videos>,
) {
    val uriHandler = LocalUriHandler.current

    val launchVideo: (String) -> Unit = { videoKey ->
        val fullUrl = BASE_URL_YOUTUBE_VIDEO + videoKey

        uriHandler.openUri(fullUrl)
    }

    videoList.forEach { video ->
        val imagePath = "${BASE_YOUTUBE_THUMBAIL_URL}${video.key}$YOUTUBE_THUMBAIL_RESOLUTION"

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Card(
                onClick = {
                    launchVideo(video.key)
                },
                colors = CardDefaults.cardColors(
                    containerColor = MainBarGreyColor,
                ),
                modifier = Modifier
                    .padding(
                        vertical = DEFAULT_PADDING.dp,
                    )
                    .fillMaxWidth(),
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = VIDEOS_BORDER_SIZE.dp,
                                    bottomStart = VIDEOS_BORDER_SIZE.dp,
                                ),
                            ),
                    ) {
                        NetworkImage(
                            imageUrl = imagePath,
                            modifier = Modifier,
                            contentScale = ContentScale.Fit,
                        )
                        Image(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(VIDEOS_PLAY_ICON_SIZE.dp),
                            painter = painterResource(resource = Res.drawable.ic_play_video),
                            contentDescription = null,
                        )
                    }
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Text(
                            text = video.name,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
