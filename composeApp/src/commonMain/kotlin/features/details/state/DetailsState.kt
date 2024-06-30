package features.details.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import common.domain.models.content.ContentCast
import common.domain.models.content.DetailedContent
import common.domain.models.content.Videos
import common.domain.models.util.LoadStatusState

data class DetailsState(
    var detailsInfo: MutableState<DetailedContent?> = mutableStateOf(null),
    var detailsCast: MutableState<List<ContentCast>> = mutableStateOf(listOf()),
    var detailsVideos: MutableState<List<Videos>> = mutableStateOf(listOf()),
) : LoadStatusState()
