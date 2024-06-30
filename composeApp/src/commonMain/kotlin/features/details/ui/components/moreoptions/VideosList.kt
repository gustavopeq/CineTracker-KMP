// package features.details.ui.components.moreoptions
//
// import android.app.Activity
// import android.content.Intent
// import android.net.Uri
// import androidx.compose.foundation.layout.Arrangement
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.fillMaxHeight
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.width
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material3.Card
// import androidx.compose.material3.CardDefaults
// import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.unit.dp
// import com.projects.moviemanager.common.ui.theme.MainBarGreyColor
// import com.projects.moviemanager.common.ui.components.NetworkImage
// import com.projects.moviemanager.common.util.UiConstants.DEFAULT_PADDING
// import com.projects.moviemanager.common.util.UiConstants.SMALL_PADDING
// import com.projects.moviemanager.common.util.UiConstants.VIDEOS_BORDER_SIZE
// import com.projects.moviemanager.common.domain.models.content.Videos
// import com.projects.moviemanager.common.util.Constants
// import com.projects.moviemanager.common.util.Constants.BASE_YOUTUBE_THUMBAIL_URL
// import com.projects.moviemanager.common.util.Constants.YOUTUBE_THUMBAIL_RESOLUTION
// import common.domain.models.content.Videos
// import common.domain.util.Constants
//
// @Composable
// @OptIn(ExperimentalMaterial3Api::class)
// fun VideoList(
//    videoList: List<Videos>
// ) {
//    val activity = LocalContext.current as Activity
//
//    val launchVideo: (String) -> Unit = { videoKey ->
//        val fullUrl = Constants.BASE_URL_YOUTUBE_VIDEO + videoKey
//        val uri = Uri.parse(fullUrl)
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        activity.startActivity(intent)
//    }
//
//    videoList.forEach { video ->
//        val imagePath = "${BASE_YOUTUBE_THUMBAIL_URL}${video.key}$YOUTUBE_THUMBAIL_RESOLUTION"
//
//        Column(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Card(
//                onClick = {
//                    launchVideo(video.key)
//                },
//                colors = CardDefaults.cardColors(
//                    containerColor = MainBarGreyColor
//                ),
//                modifier = Modifier
//                    .padding(
//                        vertical = DEFAULT_PADDING.dp
//                    )
//                    .fillMaxWidth()
//            ) {
//                Row {
//                    NetworkImage(
//                        imageUrl = imagePath,
//                        modifier = Modifier
//                            .fillMaxWidth(0.5f)
//                            .clip(
//                                RoundedCornerShape(
//                                    topStart = VIDEOS_BORDER_SIZE.dp,
//                                    bottomStart = VIDEOS_BORDER_SIZE.dp
//                                )
//                            ),
//                        contentScale = ContentScale.Fit
//                    )
//                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
//                    Column(
//                        verticalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxHeight()
//                    ) {
//                        Text(
//                            text = video.name,
//                            color = MaterialTheme.colorScheme.onPrimary,
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//            }
//        }
//    }
// }
