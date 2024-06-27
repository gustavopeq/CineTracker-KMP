package features.home.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import common.domain.models.content.GenericContent
import common.domain.models.util.LoadStatusState

data class HomeState(
    val trendingList: MutableState<List<GenericContent>> = mutableStateOf(emptyList()),
) : LoadStatusState()
